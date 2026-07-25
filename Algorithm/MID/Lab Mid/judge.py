#!/usr/bin/env python3
import argparse
import platform
import random
import re
import shutil
import subprocess
import sys
import tempfile
import shlex
from pathlib import Path

# ---------------------------------------------------------------------------
# OS detection
# ---------------------------------------------------------------------------
_SYSTEM = platform.system()
IS_WINDOWS = _SYSTEM == "Windows"
IS_UNIX = not IS_WINDOWS
EXE_EXT = ".exe" if IS_WINDOWS else ".bin"

# ANSI colors
if IS_WINDOWS:
    import os
    _ansi_ok = os.environ.get("WT_SESSION") or os.environ.get("TERM_PROGRAM") or \
               "ANSICON" in os.environ or os.environ.get("COLORTERM")
else:
    _ansi_ok = True

def _c(code: str) -> str:
    return code if _ansi_ok else ""

GREEN = _c("\033[32m")
RED = _c("\033[31m")
BOLDGREEN = _c("\033[1;32m")
BOLDRED = _c("\033[1;31m")
RESET = _c("\033[0m")

VERDICT_PRIORITY = {
    "COMPILATION ERROR": 4,
    "RUNTIME ERROR": 3,
    "TIME/MEMORY LIMIT EXCEEDED": 2,
    "WRONG ANSWER": 1,
    "ACCEPTED": 0,
}

final_results: list[str] = []

COMPILERS: dict[str, str] = {
    "g++": "g++",
    "gcc": "gcc",
    "javac": "javac",
}

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def clean_file(src: Path, dst: Path) -> None:
    lines = []
    with src.open(errors="replace") as f:
        for raw in f:
            line = re.sub(r"\s+", " ", raw).strip()
            if line:
                lines.append(line)
    with dst.open("w") as f:
        for line in lines:
            f.write(line + "\n")
        if lines:
            f.write("\n")

def files_equal(a: Path, b: Path) -> bool:
    with a.open() as fa, b.open() as fb:
        return fa.read() == fb.read()

# ---------------------------------------------------------------------------
# Process runner
# ---------------------------------------------------------------------------
def _run_process(command: list[str], stdin_path: Path, stdout_path: Path,
                time_limit: float, memory_limit_mb: int) -> int:
    try:
        if IS_UNIX:
            memory_limit_kb = memory_limit_mb * 1024
            quoted = " ".join(shlex.quote(arg) for arg in command)
            shell_cmd = f'ulimit -v {memory_limit_kb}; {quoted} < "{stdin_path}" > "{stdout_path}"'
            proc = subprocess.run(shell_cmd, shell=True, executable="/bin/sh",
                                  stderr=subprocess.DEVNULL, timeout=time_limit)
            return proc.returncode
        else:
            with stdin_path.open("rb") as fin, stdout_path.open("wb") as fout:
                proc = subprocess.run(command, stdin=fin, stdout=fout,
                                      stderr=subprocess.DEVNULL, timeout=time_limit)
                return proc.returncode
    except subprocess.TimeoutExpired:
        return 124

# ---------------------------------------------------------------------------
# Core runner (debug kept)
# ---------------------------------------------------------------------------
def run_executable(name: str, command: list[str], task_path: Path,
                   time_limit: float, memory_limit_mb: int) -> None:
    print(f"Judging {name}...")

    highest_verdict = "ACCEPTED"
    inputs = sorted(
        (task_path / "input").glob("input*.txt"),
        key=lambda p: [int(t) if t.isdigit() else t for t in re.split(r"(\d+)", p.name)],
    )

    with tempfile.TemporaryDirectory() as tmpdir:
        tmp = Path(tmpdir)
        for in_file in inputs:
            m = re.search(r"input(\d+)\.txt$", in_file.name)
            if not m:
                continue
            case_id = m.group(1)
            padded = case_id.zfill(2)

            ans_file = task_path / "answer" / f"answer{case_id}.txt"
            output_file = tmp / "output_file.txt"
            sentinel = tmp / "input_with_sentinel.txt"

            sentinel.write_text(
                in_file.read_text(errors="replace") + f"\n{random.randint(0, 32767)}\n"
            )

            status = _run_process(command, sentinel, output_file, time_limit, memory_limit_mb)

            # Windows crash diagnostics
            if IS_WINDOWS and status != 0:
                hex_code = f"0x{status:08X}"
                extra = " (Access Violation - typical segfault)" if status == 3221225477 else ""
                # print(f"  DEBUG → Windows crash code {hex_code}{extra}")
            # else:
            #     print(f"  DEBUG → Test {padded} status code = {status}  (0 = success)")

            # --- determine verdict ---
            if status in (124, 137):
                test_verdict = "TIME/MEMORY LIMIT EXCEEDED"
            elif status != 0:
                test_verdict = "RUNTIME ERROR"
            else:
                if not ans_file.exists():
                    test_verdict = "WRONG ANSWER"
                else:
                    ans_clean = tmp / "ans_clean.txt"
                    out_clean = tmp / "out_clean.txt"
                    clean_file(ans_file, ans_clean)
                    clean_file(output_file, out_clean)
                    test_verdict = "ACCEPTED" if files_equal(out_clean, ans_clean) else "WRONG ANSWER"

            if test_verdict == "ACCEPTED":
                print(f" Test {padded}: {GREEN}ACCEPTED{RESET}")
            else:
                print(f" Test {padded}: {RED}{test_verdict}{RESET}")

            if VERDICT_PRIORITY[test_verdict] > VERDICT_PRIORITY[highest_verdict]:
                highest_verdict = test_verdict
            # print()

    if highest_verdict == "ACCEPTED":
        final_results.append(f"{name}: {BOLDGREEN}{highest_verdict}{RESET}")
    else:
        final_results.append(f"{name}: {BOLDRED}{highest_verdict}{RESET}")

# ---------------------------------------------------------------------------
# Language judges — STATIC LINKING ADDED FOR WINDOWS
# ---------------------------------------------------------------------------
def judge_cpp(source_file: Path, task_path: Path, time_limit: float, mem: int) -> None:
    name = source_file.name
    executable = source_file.with_name(source_file.stem + "_cpp" + EXE_EXT)
    log = source_file.with_name(source_file.stem + "_cpp.compile.log")

    # ←←← STATIC LINKING (fixes the 0xC0000005 crash on Windows)
    compile_cmd = [COMPILERS["g++"], "-std=c++17"]
    if IS_WINDOWS:
        compile_cmd.append("-static")
    compile_cmd.extend([str(source_file), "-o", str(executable)])

    result = subprocess.run(compile_cmd, capture_output=True)
    log.write_bytes(result.stderr)

    if result.returncode != 0:
        print(f"{name}: COMPILATION ERROR (see {log})")
        final_results.append(f"{name}: {BOLDRED}COMPILATION ERROR{RESET}")
        return

    try:
        run_executable(name, [str(executable)], task_path, time_limit, mem)
    finally:
        executable.unlink(missing_ok=True)
        if log.exists() and log.stat().st_size == 0:
            log.unlink(missing_ok=True)

def judge_c(source_file: Path, task_path: Path, time_limit: float, mem: int) -> None:
    # Same static fix for C as well
    name = source_file.name
    executable = source_file.with_name(source_file.stem + "_c" + EXE_EXT)
    log = source_file.with_name(source_file.stem + "_c.compile.log")

    compile_cmd = [COMPILERS["gcc"], "-std=c11"]
    if IS_WINDOWS:
        compile_cmd.append("-static")
    compile_cmd.extend([str(source_file), "-o", str(executable)])

    result = subprocess.run(compile_cmd, capture_output=True)
    log.write_bytes(result.stderr)

    if result.returncode != 0:
        print(f"{name}: COMPILATION ERROR (see {log})")
        final_results.append(f"{name}: {BOLDRED}COMPILATION ERROR{RESET}")
        return

    try:
        run_executable(name, [str(executable)], task_path, time_limit, mem)
    finally:
        executable.unlink(missing_ok=True)
        if log.exists() and log.stat().st_size == 0:
            log.unlink(missing_ok=True)

def judge_py(source_file: Path, task_path: Path, time_limit: float, mem: int) -> None:
    python = sys.executable or "python3"
    run_executable(source_file.name, [python, str(source_file)], task_path, time_limit, mem)

def judge_java(source_file: Path, task_path: Path, time_limit: float, mem: int) -> None:
    orig_name = source_file.name
    log = source_file.with_name(source_file.stem + "_java.compile.log")

    if not re.search(r"public\s+class\s+Main", source_file.read_text(errors="replace")):
        print(f"{orig_name}: COMPILATION ERROR (Main class not found)")
        final_results.append(f"{orig_name}: {BOLDRED}COMPILATION ERROR{RESET}")
        return

    temp_source = Path("Main.java")
    shutil.copy(source_file, temp_source)
    result = subprocess.run([COMPILERS["javac"], str(temp_source)], capture_output=True)
    log.write_bytes(result.stderr)

    if result.returncode != 0:
        print(f"{orig_name}: COMPILATION ERROR (see {log})")
        final_results.append(f"{orig_name}: {BOLDRED}COMPILATION ERROR{RESET}")
        temp_source.unlink(missing_ok=True)
        return

    try:
        run_executable(orig_name, ["java", "Main"], task_path, time_limit, mem)
    finally:
        Path("Main.class").unlink(missing_ok=True)
        temp_source.unlink(missing_ok=True)
        if log.exists() and log.stat().st_size == 0:
            log.unlink(missing_ok=True)

# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------
JUDGES = {".cpp": judge_cpp, ".c": judge_c, ".py": judge_py, ".java": judge_java}

def main() -> None:
    parser = argparse.ArgumentParser(description="Competitive-programming judge")
    parser.add_argument("-path", required=True, metavar="PATH", help="Path to the task directory")
    parser.add_argument("-time", type=float, default=2.0, help="Time limit in seconds (default: 2)")
    parser.add_argument("-memory", type=int, default=256, help="Memory limit in MB (default: 256)")
    parser.add_argument("-gpp", default=None, help="Path to g++ compiler")
    parser.add_argument("-gcc", default=None, help="Path to gcc compiler")
    parser.add_argument("-javac", default=None, help="Path to javac compiler")

    args = parser.parse_args()
    task_path = Path(args.path)
    time_limit = args.time
    memory_limit = args.memory

    if args.gpp: COMPILERS["g++"] = args.gpp
    if args.gcc: COMPILERS["gcc"] = args.gcc
    if args.javac: COMPILERS["javac"] = args.javac

    if not task_path.is_dir():
        sys.exit(f"error: '{task_path}' is not a directory")

    print(f"Platform : {_SYSTEM}")
    print(f"Time     : {time_limit}s")
    print(f"Memory   : {memory_limit}MB")
    print(f"g++      : {COMPILERS['g++']}")
    print(f"gcc      : {COMPILERS['gcc']}")
    print(f"javac    : {COMPILERS['javac']}")
    print()

    submissions = sorted(
        (f for f in (task_path / "submission").rglob("*")
         if f.is_file() and f.suffix in JUDGES),
        key=lambda p: [int(t) if t.isdigit() else t for t in re.split(r"(\d+)", str(p))],
    )

    if not submissions:
        sys.exit(f"error: no supported source files found in '{task_path / 'submission'}'")

    for source_file in submissions:
        JUDGES[source_file.suffix](source_file, task_path, time_limit, memory_limit)

    print("================== FINAL VERDICTS ==================")
    for result in final_results:
        print(f" {result}")
    print()

if __name__ == "__main__":
    main()