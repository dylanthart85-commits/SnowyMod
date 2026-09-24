# Snowy Biomes (Fabric mod)

Kleine **client-side** Fabric-mod die permanent sneeuw-particles laat vallen,
maar uitsluitend in biomen waar het volgens vanilla-logica ook echt zou
sneeuwen (ijs- en sneeuwbiomen). Elders gebeurt niets — geen regen, geen
verandering aan het echte weersysteem.

## Voordat je bouwt — controleer dit eerst

Ik heb dit project geschreven zonder internettoegang, dus ik kon de exacte
versienummers voor **Minecraft 1.21.11** en **Fabric Loader 0.19.5** niet
verifiëren. Ga naar https://fabricmc.net/develop en vul in `gradle.properties`
de juiste waarden in voor:

- `yarn_mappings`
- `loader_version`
- `fabric_version` (Fabric API)

Als de klasse `SnowyBiomesClient` niet compileert (bijvoorbeeld een foutmelding
bij `biome.getPrecipitation(pos)`), zoek dan in je IDE naar de juiste
methodenaam op de `Biome`-klasse in de mappings die je gebruikt — deze kan
per versie licht anders heten (bijv. `getPrecipitationAt`).

## Bouwen — optie A: op je eigen computer

```bash
./gradlew build
```

Het resulterende jar-bestand vind je in `build/libs/`. Zet dat in de `mods`
map van een Fabric-clientinstallatie (met bijpassende Fabric API mod erbij).

## Bouwen — optie B: automatisch via GitHub Actions (geen eigen installatie nodig)

Dit project bevat een kant-en-klare workflow (`.github/workflows/build.yml`)
die de mod in de cloud bouwt zonder dat je zelf Java of Gradle hoeft te
installeren.

1. Maak een gratis GitHub-account (indien je die nog niet hebt) en maak een
   nieuwe, publieke of privé repository aan.
2. Upload de inhoud van deze map naar die repository (via de "Add file" →
   "Upload files" knop op github.com, of met `git push`).
3. Ga naar het tabblad **Actions** van je repository. De workflow "Build mod"
   start automatisch.
4. Wacht tot de run groen is (klaar). Klik erop, en onderaan bij
   **Artifacts** staat `snowybiomes-jar` — download dat zip-bestand, daarin
   zit je `.jar`.

Let op: als het bouwen faalt, is de kans het grootst dat het Yarn
build-nummer in `gradle.properties` niet klopt (zie de opmerkingen daar) —
pas dat aan en push opnieuw.

## Hoe het werkt

Elke client-tick worden er een paar willekeurige punten rondom de speler
gekozen. Voor elk punt wordt gecheckt of de biome op dat punt "sneeuw" als
neerslagtype heeft (dezelfde check die vanilla gebruikt om te bepalen of
neerslag als sneeuw of regen valt). Zo ja, dan wordt er een `SNOWFLAKE`
particle boven dat punt gespawnd die naar beneden valt.

Instelbare waarden bovenaan `SnowyBiomesClient.java`:

- `PARTICLES_PER_TICK` — hoeveelheid particles per tick (dichtheid van de sneeuw)
- `RADIUS` — hoe ver rond de speler er gezocht wordt
- `SPAWN_HEIGHT_ABOVE` — hoogte boven het terrein waar de sneeuw begint te vallen
