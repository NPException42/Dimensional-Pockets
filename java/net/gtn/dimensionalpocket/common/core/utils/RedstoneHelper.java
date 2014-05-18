package net.gtn.dimensionalpocket.common.core.utils;



public class RedstoneHelper {
//    public static void checkNeighboursAndUpdateInputStrength(IBlockAccess world, int x, int y, int z) {
//        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
//            int neighbourX = x + direction.offsetX;
//            int neighbourY = y + direction.offsetY;
//            int neighbourZ = z + direction.offsetZ;
//
//            Block neighbourBlock = world.getBlock(neighbourX, neighbourY, neighbourZ);
//
//            int weak = neighbourBlock.isProvidingWeakPower(world, neighbourX, neighbourY, neighbourZ, direction.ordinal());
//            int strong = neighbourBlock.isProvidingStrongPower(world, neighbourX, neighbourY, neighbourZ, direction.ordinal());
//            int strength = Math.max(weak, strong);
//
//            TileEntity tileEntity = world.getTileEntity(x, y, z);
//            TileDimensionalPocket tile = (TileDimensionalPocket) tileEntity;
//
//            tile.getPocket().setInputSignal(direction.ordinal(), strength);
//            DPLogger.info("Changed inputsignal: " + direction.name() + " to " + strength);
//        }
//    }
//
//    public static void checkWallNeighbourAndUpdateOutputStrength(IBlockAccess world, int x, int y, int z) {
//        ForgeDirection wallSide = Pocket.getSideForBlock(new CoordSet(x, y, z).asSpawnPoint());
//        ForgeDirection neighbourSide = wallSide.getOpposite();
//
//        int neighbourX = x + neighbourSide.offsetX;
//        int neighbourY = y + neighbourSide.offsetY;
//        int neighbourZ = z + neighbourSide.offsetZ;
//
//        Block neighbourBlock = world.getBlock(neighbourX, neighbourY, neighbourZ);
//
//        int weak = neighbourBlock.isProvidingWeakPower(world, neighbourX, neighbourY, neighbourZ, neighbourSide.ordinal());
//        int strong = neighbourBlock.isProvidingStrongPower(world, neighbourX, neighbourY, neighbourZ, neighbourSide.ordinal());
//        int strength = Math.max(weak, strong);
//
//        Pocket pocket = PocketRegistry.getPocket(new CoordSet(x, y, z).asChunkCoords());
//
//        if (pocket == null)
//            return;
//
//        pocket.setOutputSignal(wallSide.ordinal(), strength);
//        DPLogger.info("Changed outputsignal: " + wallSide.name() + " to " + strength);
//
//        World srcWorld = MinecraftServer.getServer().worldServerForDimension(pocket.getBlockDim());
//        srcWorld.func_147453_f(x, y, z, ModBlocks.dimensionalPocket);
//        srcWorld.notifyBlocksOfNeighborChange(x, y, z, ModBlocks.dimensionalPocket);
//    }
}
