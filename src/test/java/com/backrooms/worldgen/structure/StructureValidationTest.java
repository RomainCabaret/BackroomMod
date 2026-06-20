package com.backrooms.worldgen.structure;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class StructureValidationTest {

    // On reprend ta Regex exacte
    private static final Pattern PATTERN_STRUCTURE = Pattern.compile(
            "(.+)_(\\d+)_(\\d+)_(\\d+)_n_([a-z0-9-]+)_s_([a-z0-9-]+)_e_([a-z0-9-]+)_o_([a-z0-9-]+)(?:_v\\d+)?"
    );

    @Test
    public void validerToutesLesStructures() throws Exception {
        // Ajuste le chemin si tes NBT sont dans 'data' au lieu de 'assets'
        Path dossierStructures = Paths.get("src/main/resources/data/backrooms/structure");
        assertTrue(Files.exists(dossierStructures), "Le dossier des structures est introuvable à ce chemin !");

        Map<String, List<int[]>> megastructures = new HashMap<>();
        int totalFichiers = 0;

        try (Stream<Path> paths = Files.walk(dossierStructures)) {
            List<Path> fichiersNbt = paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".nbt"))
                    .toList();

            for (Path fichier : fichiersNbt) {
                String nomFichier = fichier.getFileName().toString().replace(".nbt", "");
                Matcher matcher = PATTERN_STRUCTURE.matcher(nomFichier);

                // 1. VÉRIFICATION DU NOM (Liaisons et nomenclature)
                assertTrue(matcher.matches(),
                        "NOM INVALIDE : '" + nomFichier + "' va faire crasher le routeur.");

                String nomDeBase = matcher.group(1);
                int ix = Integer.parseInt(matcher.group(2));
                int iy = Integer.parseInt(matcher.group(3));
                int iz = Integer.parseInt(matcher.group(4));

                megastructures.computeIfAbsent(nomDeBase, k -> new ArrayList<>()).add(new int[]{ix, iy, iz});
                totalFichiers++;
            }
        }

        assertTrue(totalFichiers > 0, "Aucun fichier NBT trouvé, le test ne sert à rien.");

        // 2. VÉRIFICATION DE L'INTÉGRITÉ DES MÉGASTRUCTURES (Trou dans la matrice)
        for (Map.Entry<String, List<int[]>> entry : megastructures.entrySet()) {
            String nom = entry.getKey();
            List<int[]> morceaux = entry.getValue();

            int maxX = morceaux.stream().mapToInt(m -> m[0]).max().orElse(0);
            int maxY = morceaux.stream().mapToInt(m -> m[1]).max().orElse(0);
            int maxZ = morceaux.stream().mapToInt(m -> m[2]).max().orElse(0);

            // Si au moins un index est supérieur à 0, c'est une mégastructure de type puzzle
            if (maxX > 0 || maxY > 0 || maxZ > 0) {
                int morceauxAttendus = (maxX + 1) * (maxY + 1) * (maxZ + 1);
                assertEquals(morceauxAttendus, morceaux.size(),
                        "PIÈCE MANQUANTE : La mégastructure '" + nom + "' devrait avoir "
                                + morceauxAttendus + " morceaux, mais elle n'en a que " + morceaux.size()
                                + ". Il te manque un fichier .nbt !");
            }
        }
    }
    @Test
    public void validerAucuneCombinaisonManquante() throws Exception {
        Path dossierStructures = Paths.get("src/main/resources/data/backrooms/structure");

        Map<String, List<String[]>> piecesParNom = new HashMap<>();
        java.util.Set<String> signaturesUniques = new java.util.HashSet<>();

        // 1. Lecture de toutes les pièces
        try (Stream<Path> paths = Files.walk(dossierStructures)) {
            List<Path> fichiersNbt = paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".nbt"))
                    .toList();

            for (Path fichier : fichiersNbt) {
                Matcher matcher = PATTERN_STRUCTURE.matcher(fichier.getFileName().toString().replace(".nbt", ""));
                if (matcher.matches()) {
                    String nomDeBase = matcher.group(1);
                    String n = matcher.group(5), s = matcher.group(6), e = matcher.group(7), o = matcher.group(8);

                    piecesParNom.computeIfAbsent(nomDeBase, k -> new ArrayList<>())
                            .add(new String[]{matcher.group(2), matcher.group(3), matcher.group(4), n, s, e, o});

                    signaturesUniques.add(n);
                    signaturesUniques.add(s);
                    signaturesUniques.add(e);
                    signaturesUniques.add(o);
                }
            }
        }

        // 2. Construction du catalogue simulé (avec les 4 rotations) pour les petites salles
        List<String[]> catalogueSimule = new ArrayList<>();

        for (Map.Entry<String, List<String[]>> entry : piecesParNom.entrySet()) {
            List<String[]> morceaux = entry.getValue();
            boolean isMega = morceaux.stream().anyMatch(m -> !m[0].equals("0") || !m[1].equals("0") || !m[2].equals("0"));

            if (!isMega) {
                String n = morceaux.get(0)[3], s = morceaux.get(0)[4], e = morceaux.get(0)[5], o = morceaux.get(0)[6];

                catalogueSimule.add(new String[]{n, s, e, o}); // NONE
                catalogueSimule.add(new String[]{o, e, n, s}); // CLOCKWISE_90
                catalogueSimule.add(new String[]{s, n, o, e}); // CLOCKWISE_180
                catalogueSimule.add(new String[]{e, o, s, n}); // COUNTERCLOCKWISE_90
            }
        }

        java.util.Set<String> piecesPhysiquesAConstruire = new java.util.HashSet<>();
        List<String> toDoList = new ArrayList<>();

        for (String reqN : signaturesUniques) {
            for (String reqS : signaturesUniques) {
                for (String reqE : signaturesUniques) {
                    for (String reqO : signaturesUniques) {

                        boolean pieceTrouvee = false;
                        for (String[] piece : catalogueSimule) {
                            if (piece[0].equals(reqN) && piece[1].equals(reqS) &&
                                    piece[2].equals(reqE) && piece[3].equals(reqO)) {
                                pieceTrouvee = true;
                                break;
                            }
                        }

                        if (!pieceTrouvee) {
                            // Traduction pour les humains
                            String n = reqN.startsWith("7x1") ? "Porte" : (reqN.equals("0") ? "Mur" : reqN);
                            String s = reqS.startsWith("7x1") ? "Porte" : (reqS.equals("0") ? "Mur" : reqS);
                            String e = reqE.startsWith("7x1") ? "Porte" : (reqE.equals("0") ? "Mur" : reqE);
                            String o = reqO.startsWith("7x1") ? "Porte" : (reqO.equals("0") ? "Mur" : reqO);

                            // On simule les 4 rotations pour identifier la pièce "physique" unique
                            String r1 = n + "," + s + "," + e + "," + o;
                            String r2 = o + "," + e + "," + n + "," + s;
                            String r3 = s + "," + n + "," + o + "," + e;
                            String r4 = e + "," + o + "," + s + "," + n;

                            // On prend la plus petite chaîne comme ID unique de cette forme
                            String idPhysique = java.util.stream.Stream.of(r1, r2, r3, r4).min(String::compareTo).get();

                            // Si on n'a pas encore listé cette pièce exacte (ou une de ses rotations)
                            if (piecesPhysiquesAConstruire.add(idPhysique)) {
                                toDoList.add(String.format(" 🛠️  Construire une pièce avec ->  Nord: %-5s | Sud: %-5s | Est: %-5s | Ouest: %-5s", n, s, e, o));
                            }
                        }
                    }
                }
            }
        }

        // 4. Le verdict propre
        assertTrue(toDoList.isEmpty(),
                "\n\n🚨 CRASH DE GÉNÉRATION PRÉVU !\nIl te manque des salles de transition. Va en jeu et génère ces pièces exactes :\n\n" +
                        String.join("\n", toDoList) + "\n\n");
    }
}