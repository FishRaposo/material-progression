# Material Progression

An open-source NeoForge mod for Minecraft 26.2 that rebuilds material progression
around useful metals, alloys, processing, and meaningful equipment choices.

`Material Progression` is a working title. The stable internal mod ID is
`material_progression`.

## MVP: Bronze

The first vertical slice implements:

- Tin ore generation and raw tin
- A fuel-burning stone crusher
- `1 raw metal / ore -> 2 dust`
- Copper, tin, and bronze dust
- `3 copper dust + 1 tin dust -> 4 bronze dust`
- Smelting dust into ingots
- Tin and bronze tool sets

The current item visuals deliberately reuse vanilla textures. They are development
placeholders, not final art.

## Requirements

- Minecraft 26.2
- NeoForge 26.2.0.35-beta or newer compatible 26.2 build
- Java 25

## Development

Clone the repository and run:

```bash
./gradlew build
./gradlew runClient
```

On Windows:

```powershell
./gradlew.bat build
./gradlew.bat runClient
```

The built mod JAR appears in `build/libs`.

## Provisional balance

Tin is currently weaker than stone (`96` durability, `3.5` mining speed). Bronze
is currently an iron-like manufactured material (`325` durability, `6.5` mining
speed). These values are explicit MVP tuning points, not settled design.

## License

[MIT](LICENSE)
