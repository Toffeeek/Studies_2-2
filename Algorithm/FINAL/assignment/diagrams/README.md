# Flood Evacuation Report Diagrams

Use the PNG files for inserting into LibreOffice Writer. The SVG files are editable source versions.

These diagrams are simplified for report layout: no large headings, larger internal labels, and only essential explanatory text.

| File | Best placement | What it illustrates |
| --- | --- | --- |
| `01_system_pipeline.png` | After the problem description or before "Algorithmic Components" | Overall flow from city/flood/resource inputs to evacuation, relief, and risk-map outputs. |
| `02_road_reachability_bfs_dfs.png` | Beside the BFS/DFS paragraph | Reachable and isolated zones after flooded roads are removed. |
| `03_safe_route_selection.png` | Beside the Dijkstra or Bellman-Ford paragraph | Why the safest route can differ from the visually shortest route when risk is part of edge cost. |
| `04_convex_hull_flood_boundary.png` | Beside the Convex Hull paragraph | Reported flood points and the estimated outer danger boundary. |
| `05_relief_knapsack_loading.png` | Beside the 0/1 Knapsack paragraph | Selecting high-value relief items under vehicle capacity limits. |
| `06_emergency_mst_network.png` | Beside the MST paragraph | Minimum-cost temporary links among shelters, warehouses, and control center. |
