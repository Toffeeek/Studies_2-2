package com.example.wordle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class WordRepository {
    private static final List<WordEntry> ANSWERS = List.of(
            new WordEntry("APPLE", "A round fruit with crisp flesh."),
            new WordEntry("BRAVE", "Ready to face danger or difficulty."),
            new WordEntry("CHAIR", "A seat for one person with a back."),
            new WordEntry("DREAM", "A series of thoughts or images during sleep."),
            new WordEntry("EAGER", "Strongly wanting to do or have something."),
            new WordEntry("FLAME", "The visible, hot part of a fire."),
            new WordEntry("GRACE", "Simple elegance or courteous goodwill."),
            new WordEntry("HOUSE", "A building where people live."),
            new WordEntry("IVORY", "A hard white material, or its color."),
            new WordEntry("JOLLY", "Happy and cheerful."),
            new WordEntry("KNIFE", "A tool with a sharp blade."),
            new WordEntry("LIGHT", "Brightness that makes sight possible."),
            new WordEntry("MANGO", "A sweet tropical fruit."),
            new WordEntry("NURSE", "A person trained to care for the sick."),
            new WordEntry("OCEAN", "A very large body of salt water."),
            new WordEntry("PLANT", "A living thing that grows in soil."),
            new WordEntry("QUEEN", "A female monarch."),
            new WordEntry("RIVER", "A large natural stream of water."),
            new WordEntry("STONE", "Hard solid mineral matter."),
            new WordEntry("TRAIN", "A connected line of railway vehicles."),
            new WordEntry("UNITY", "The state of being joined as one."),
            new WordEntry("VOICE", "Sound produced when speaking or singing."),
            new WordEntry("WATER", "A clear liquid essential for life."),
            new WordEntry("YEAST", "A fungus used in baking and brewing."),
            new WordEntry("ZEBRA", "An African wild horse with black-and-white stripes."),
            new WordEntry("LIGMA", "Ligma balls lmao gotem.")
    );

    private static final Set<String> EXTRA_GUESSES = Set.of(
            "ABOUT", "ABOVE", "ACTOR", "ACUTE", "ADMIT", "ADOPT", "ADULT", "AFTER", "AGAIN", "AGENT",
            "AGREE", "AHEAD", "ALARM", "ALBUM", "ALERT", "ALIEN", "ALIVE", "ALLOW", "ALONE", "ALONG",
            "ALTER", "AMONG", "ANGER", "ANGLE", "ANGRY", "APART", "ARENA", "ARGUE", "ARISE", "ARRAY",
            "ARROW", "ASIDE", "AUDIO", "AVOID", "AWARD", "AWARE", "BADLY", "BASIC", "BEACH", "BEGIN",
            "BEING", "BELOW", "BENCH", "BIRTH", "BLACK", "BLAME", "BLIND", "BLOCK", "BLOOD", "BOARD",
            "BRAIN", "BREAD", "BREAK", "BROWN", "BUILD", "BUYER", "CABLE", "CARRY", "CATCH", "CAUSE",
            "CHAIN", "CHEST", "CHIEF", "CHILD", "CIVIL", "CLAIM", "CLASS", "CLEAN", "CLEAR", "CLIMB",
            "CLOCK", "CLOSE", "COACH", "COAST", "COULD", "COUNT", "COURT", "COVER", "CRAFT", "CRASH",
            "CREAM", "CRIME", "CROSS", "CROWD", "CROWN", "DAILY", "DANCE", "DEATH", "DEPTH", "DOING",
            "DOUBT", "DOZEN", "DRAFT", "DRAMA", "DRINK", "DRIVE", "EARLY", "EARTH", "EIGHT", "ELITE",
            "EMPTY", "ENEMY", "ENJOY", "ENTER", "ENTRY", "EQUAL", "ERROR", "EVENT", "EVERY", "EXACT",
            "EXIST", "EXTRA", "FAITH", "FALSE", "FAULT", "FIBER", "FIELD", "FIFTH", "FIFTY", "FIGHT",
            "FINAL", "FIRST", "FIXED", "FLASH", "FLEET", "FLOOR", "FOCUS", "FORCE", "FORTH", "FORTY",
            "FOUND", "FRAME", "FRANK", "FRESH", "FRONT", "FRUIT", "FULLY", "FUNNY", "GIANT", "GIVEN",
            "GLASS", "GLOBE", "GOING", "GRAND", "GRANT", "GRASS", "GREAT", "GREEN", "GROUP", "GUARD",
            "GUESS", "GUEST", "GUIDE", "HAPPY", "HEART", "HEAVY", "HORSE", "HOTEL", "IMAGE", "INDEX",
            "INNER", "INPUT", "ISSUE", "JOINT", "JUDGE", "KNOWN", "LABEL", "LARGE", "LATER", "LAUGH",
            "LAYER", "LEARN", "LEAST", "LEAVE", "LEGAL", "LEVEL", "LOCAL", "LOGIC", "LOOSE", "LOWER",
            "LUCKY", "LUNCH", "MAGIC", "MAJOR", "MAKER", "MARCH", "MATCH", "MAYBE", "METAL", "MIGHT",
            "MINOR", "MODEL", "MONEY", "MONTH", "MOTOR", "MOUSE", "MOUTH", "MOVIE", "MUSIC", "NEEDS",
            "NEVER", "NIGHT", "NOISE", "NORTH", "NOVEL", "OFFER", "OFTEN", "ORDER", "OTHER", "PAINT",
            "PANEL", "PAPER", "PARTY", "PEACE", "PHASE", "PHONE", "PHOTO", "PIECE", "PILOT", "PITCH",
            "PLACE", "PLAIN", "PLANE", "POINT", "POUND", "POWER", "PRESS", "PRICE", "PRIDE", "PRIME",
            "PRINT", "PRIOR", "PRIZE", "PROOF", "PROUD", "PROVE", "QUICK", "QUIET", "QUITE", "RADIO",
            "RAISE", "RANGE", "RAPID", "RATIO", "REACH", "READY", "REFER", "RIGHT", "ROUND", "ROUTE",
            "ROYAL", "RURAL", "SCALE", "SCENE", "SCOPE", "SCORE", "SENSE", "SERVE", "SEVEN", "SHALL",
            "SHAPE", "SHARE", "SHARP", "SHEET", "SHELF", "SHIFT", "SHIRT", "SHOCK", "SHORT", "SHOWN",
            "SIGHT", "SINCE", "SKILL", "SLEEP", "SMALL", "SMART", "SMILE", "SMITH", "SOLID", "SOLVE",
            "SORRY", "SOUND", "SOUTH", "SPACE", "SPARE", "SPEAK", "SPEED", "SPEND", "SPENT", "SPLIT",
            "SPORT", "STAFF", "STAGE", "STAKE", "STAND", "START", "STATE", "STEAM", "STEEL", "STICK",
            "STILL", "STOCK", "STORE", "STORM", "STORY", "STRIP", "STUCK", "STUDY", "STUFF", "STYLE",
            "SUGAR", "TABLE", "TAKEN", "TASTE", "TEACH", "THANK", "THEIR", "THEME", "THERE", "THICK",
            "THING", "THINK", "THIRD", "THOSE", "THREE", "THROW", "TIGHT", "TIMES", "TIRED", "TITLE",
            "TODAY", "TOPIC", "TOTAL", "TOUCH", "TOUGH", "TOWER", "TRACK", "TRADE", "TRIAL", "TRIED",
            "TRUCK", "TRULY", "TRUST", "TRUTH", "TWICE", "UNDER", "UPPER", "UPSET", "URBAN", "USAGE",
            "USUAL", "VALID", "VALUE", "VIDEO", "VISIT", "VITAL", "WASTE", "WATCH", "WHEEL", "WHERE",
            "WHICH", "WHILE", "WHITE", "WHOLE", "WHOSE", "WOMAN", "WORLD", "WORRY", "WORTH", "WOULD",
            "WRITE", "WRONG", "YOUNG"
    );

    private final Random random = new Random();
    private final Map<String, WordEntry> answersByWord;
    private final Set<String> validGuesses;

    public WordRepository() {
        answersByWord = ANSWERS.stream().collect(java.util.stream.Collectors.toUnmodifiableMap(WordEntry::word, entry -> entry));
        List<String> allGuesses = new ArrayList<>(EXTRA_GUESSES);
        allGuesses.addAll(answersByWord.keySet());
        validGuesses = Set.copyOf(allGuesses);
    }

    public WordEntry randomAnswer() {
        return ANSWERS.get(random.nextInt(ANSWERS.size()));
    }

    public boolean isValidGuess(String guess) {
        return validGuesses.contains(guess);
    }

    public record WordEntry(String word, String meaning) {
    }
}
