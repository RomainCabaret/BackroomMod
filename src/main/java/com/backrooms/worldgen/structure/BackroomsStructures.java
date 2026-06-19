package com.backrooms.worldgen.structure;

import com.backrooms.Backrooms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackroomsStructures {

    public static class ModuleInfo {
        public final ResourceLocation id;
        public final String nomDeBase;
        public final int indexX, indexY, indexZ;
        public final String sigN, sigS, sigE, sigO;
        public final Rotation rotation;

        public ModuleInfo(ResourceLocation id, String nomDeBase, int indexX, int indexY, int indexZ,
                          String sigN, String sigS, String sigE, String sigO, Rotation rotation) {
            this.id = id;
            this.nomDeBase = nomDeBase;
            this.indexX = indexX;
            this.indexY = indexY;
            this.indexZ = indexZ;
            this.sigN = sigN;
            this.sigS = sigS;
            this.sigE = sigE;
            this.sigO = sigO;
            this.rotation = rotation;
        }
    }

    // NOUVEAU : La classe qui regroupe les tranches d'une grosse structure
    public static class Megastructure {
        public final String nom;
        public final int tailleX; // Largeur en chunks
        public final int tailleY; // Hauteur en chunks
        public final int tailleZ; // Profondeur en chunks
        public final List<ModuleInfo> morceaux;

        public Megastructure(String nom, List<ModuleInfo> morceaux) {
            this.nom = nom;
            this.morceaux = morceaux;
            int mx = 0, my = 0, mz = 0;
            for (ModuleInfo m : morceaux) {
                if (m.indexX > mx) mx = m.indexX;
                if (m.indexY > my) my = m.indexY;
                if (m.indexZ > mz) mz = m.indexZ;
            }
            // Si l'index max est 1, la taille est 2.
            this.tailleX = mx + 1;
            this.tailleY = my + 1;
            this.tailleZ = mz + 1;
        }

        public ModuleInfo getMorceau(int ix, int iy, int iz) {
            for (ModuleInfo m : morceaux) {
                if (m.indexX == ix && m.indexY == iy && m.indexZ == iz) return m;
            }
            return null; // Morceau manquant ou vide
        }
    }

    public static final List<ModuleInfo> CATALOGUE_NIVEAU_0 = new ArrayList<>();
    public static final List<Megastructure> CATALOGUE_MEGASTRUCTURES = new ArrayList<>();

    private static final Pattern PATTERN_STRUCTURE = Pattern.compile(
            "(.+)_(\\d+)_(\\d+)_(\\d+)_n_([a-z0-9-]+)_s_([a-z0-9-]+)_e_([a-z0-9-]+)_o_([a-z0-9-]+)"
    );

    public static void chargerStructuresDynamiques(net.minecraft.server.MinecraftServer server) {
        CATALOGUE_NIVEAU_0.clear();
        CATALOGUE_MEGASTRUCTURES.clear();
        Map<String, List<ModuleInfo>> trieur = new HashMap<>();

        net.minecraft.server.packs.resources.ResourceManager manager = server.getResourceManager();

        try {
            Backrooms.LOGGER.info("[Backrooms-Radar] 🔍 Scan des NBT...");
            var fichiers = manager.listResources("structure", path -> path.getPath().endsWith(".nbt"));

            for (var entry : fichiers.entrySet()) {
                ResourceLocation location = entry.getKey();
                String fullPath = location.getPath();
                String nomFichierSansExtension = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length() - 4);

                Matcher matcher = PATTERN_STRUCTURE.matcher(nomFichierSansExtension);

                if (matcher.matches()) {
                    String nomDeBase = matcher.group(1);
                    int ix = Integer.parseInt(matcher.group(2));
                    int iy = Integer.parseInt(matcher.group(3));
                    int iz = Integer.parseInt(matcher.group(4));
                    String n = matcher.group(5);
                    String s = matcher.group(6);
                    String e = matcher.group(7);
                    String o = matcher.group(8);

                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), nomFichierSansExtension);
                    ModuleInfo piece = new ModuleInfo(id, nomDeBase, ix, iy, iz, n, s, e, o, Rotation.NONE);

                    // On range la pièce dans le carton portant son nom de base
                    trieur.computeIfAbsent(nomDeBase, k -> new ArrayList<>()).add(piece);
                }
            }

            // On vide les cartons
            for (Map.Entry<String, List<ModuleInfo>> dossier : trieur.entrySet()) {
                List<ModuleInfo> morceaux = dossier.getValue();
                String nom = dossier.getKey();

                // Est-ce une mégastructure ? (A-t-elle des index > 0 ?)
                boolean isMega = morceaux.stream().anyMatch(m -> m.indexX > 0 || m.indexY > 0 || m.indexZ > 0);

                if (isMega) {
                    // C'est un puzzle géant. On ne le tourne pas, on le stocke tel quel.
                    CATALOGUE_MEGASTRUCTURES.add(new Megastructure(nom, morceaux));
                    Backrooms.LOGGER.info("[Backrooms-Radar] 🏢 Mégastructure validée : " + nom + " (" + morceaux.size() + " tranches)");
                } else {
                    // C'est une petite salle 1x1. On génère ses 4 rotations.
                    ModuleInfo originale = morceaux.get(0);
                    int ix = originale.indexX;
                    int iy = originale.indexY;
                    int iz = originale.indexZ;
                    ResourceLocation id = originale.id;

                    CATALOGUE_NIVEAU_0.add(originale);
                    CATALOGUE_NIVEAU_0.add(new ModuleInfo(id, nom, ix, iy, iz, originale.sigO, originale.sigE, originale.sigN, originale.sigS, Rotation.CLOCKWISE_90));
                    CATALOGUE_NIVEAU_0.add(new ModuleInfo(id, nom, ix, iy, iz, originale.sigS, originale.sigN, originale.sigO, originale.sigE, Rotation.CLOCKWISE_180));
                    CATALOGUE_NIVEAU_0.add(new ModuleInfo(id, nom, ix, iy, iz, originale.sigE, originale.sigO, originale.sigS, originale.sigN, Rotation.COUNTERCLOCKWISE_90));
                }
            }

        } catch (Exception e) {
            Backrooms.LOGGER.error("[Backrooms-Radar] 💥 Crash pendant le chargement : ", e);
        }

        Backrooms.LOGGER.info("[Backrooms-Radar] 📊 Petites pièces : " + CATALOGUE_NIVEAU_0.size() + " | Mégastructures : " + CATALOGUE_MEGASTRUCTURES.size());
    }

    public static List<ModuleInfo> trouverPiecesCompatibles(String requisN, String requisS, String requisE, String requisO) {
        List<ModuleInfo> compatibles = new ArrayList<>();

        for (ModuleInfo m : CATALOGUE_NIVEAU_0) {
            if (requisN != null && !m.sigN.equals(requisN)) continue;
            if (requisS != null && !m.sigS.equals(requisS)) continue;
            if (requisE != null && !m.sigE.equals(requisE)) continue;
            if (requisO != null && !m.sigO.equals(requisO)) continue;

            compatibles.add(m);
        }

        return compatibles;
    }
}