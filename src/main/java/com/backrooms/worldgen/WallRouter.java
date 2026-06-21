package com.backrooms.worldgen;

import com.backrooms.worldgen.structure.BackroomsStructures;

public class WallRouter {

    public static String getEdge(int x1, int z1, int x2, int z2, long seed) {
        // 1. Priorité absolue aux Mégastructures
        MegaManager.MegaHit megaVoisin = MegaManager.check(x2, z2, seed);
        if (megaVoisin != null) {
            int rezDeChaussee = megaVoisin.mega().tailleY - 1;
            BackroomsStructures.ModuleInfo pieceMega = megaVoisin.mega().getMorceau(megaVoisin.ix(), rezDeChaussee, megaVoisin.iz());

            if (pieceMega != null && pieceMega.id != null) {
                String nom = pieceMega.id.getPath();
                String sig = "0";
                if (z2 < z1) sig = extraireSignature(nom, "_s_");
                else if (z2 > z1) sig = extraireSignature(nom, "_n_");
                else if (x2 > x1) sig = extraireSignature(nom, "_o_");
                else if (x2 < x1) sig = extraireSignature(nom, "_e_");

                if (!sig.equals("0")) return sig;
            }
        }

        LayoutProvider.Zone zone1 = LayoutProvider.getZone(x1, z1, seed);
        LayoutProvider.Zone zone2 = LayoutProvider.getZone(x2, z2, seed);

        // 2. OPEN <-> OPEN : On ouvre en grand
        if (zone1 == LayoutProvider.Zone.OPEN && zone2 == LayoutProvider.Zone.OPEN) {
            return "open";
        }

        // 3. MAZE <-> MAZE : Ton algorithme de labyrinthe garanti sans culs-de-sac
        if (zone1 == LayoutProvider.Zone.MAZE && zone2 == LayoutProvider.Zone.MAZE) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minZ = Math.min(z1, z2);
            int maxZ = Math.max(z1, z2);

            long chunkHash = seed ^ (maxX * 89513L) ^ (maxZ * 46831L);
            net.minecraft.util.RandomSource chunkRand = net.minecraft.util.RandomSource.create(chunkHash);
            boolean forceNord = chunkRand.nextBoolean();

            if (minX == maxX && minZ == maxZ - 1 && forceNord) return "7x1-8x1-7x2-8x2";
            if (minZ == maxZ && minX == maxX - 1 && !forceNord) return "7x1-8x1-7x2-8x2";

            long edgeHash = seed ^ (minX * 12345L) ^ (maxX * 67890L) ^ (minZ * 54321L) ^ (maxZ * 9876L);
            net.minecraft.util.RandomSource edgeRand = net.minecraft.util.RandomSource.create(edgeHash);

            return edgeRand.nextFloat() < 0.35f ? "7x1-8x1-7x2-8x2" : "0";
        }

        // 4. OPEN <-> MAZE : La nouvelle connexion (Salles de transition)
        // 50% de chance d'avoir une porte qui donne sur l'open space, sinon un mur plein
        long transitionHash = seed ^ ((x1 + x2) * 81347L) ^ ((z1 + z2) * 4307L);
        net.minecraft.util.RandomSource transitionRand = net.minecraft.util.RandomSource.create(transitionHash);

        return transitionRand.nextFloat() < 0.5f ? "7x1-8x1-7x2-8x2" : "0";
    }

    private static String extraireSignature(String nomFichier, String face) {
        try {
            int start = nomFichier.indexOf(face) + 3;
            int end = nomFichier.indexOf("_", start);
            if (end == -1) end = nomFichier.indexOf(".", start);
            if (end == -1) end = nomFichier.length();
            return nomFichier.substring(start, end);
        } catch (Exception e) {
            return "0";
        }
    }
}