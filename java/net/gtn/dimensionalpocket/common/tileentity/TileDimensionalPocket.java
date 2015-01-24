package net.gtn.dimensionalpocket.common.tileentity;

import me.jezza.oc.common.interfaces.IBlockInteract;
import me.jezza.oc.common.interfaces.IBlockNotifier;
import me.jezza.oc.common.utils.CoordSet;
import net.gtn.dimensionalpocket.client.utils.UtilsFX;
import net.gtn.dimensionalpocket.common.ModBlocks;
import net.gtn.dimensionalpocket.common.core.ChunkLoaderHandler;
import net.gtn.dimensionalpocket.common.core.pocket.Pocket;
import net.gtn.dimensionalpocket.common.core.pocket.PocketRegistry;
import net.gtn.dimensionalpocket.common.core.pocket.PocketTeleportPreparation;
import net.gtn.dimensionalpocket.common.core.pocket.PocketTeleportPreparation.Direction;
import net.gtn.dimensionalpocket.common.core.utils.Utils;
import net.gtn.dimensionalpocket.common.items.handlers.NetherCrystalHandler;
import net.gtn.dimensionalpocket.common.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileDimensionalPocket extends TileDP implements IBlockNotifier, IBlockInteract, IEnergyReceiver, IEnergyProvider, IEnergyHandler {

    private static final String TAG_CUSTOM_DP_NAME = "customDPName";

    @SideOnly(Side.CLIENT)
    private Pocket pocket;
    
    private String customName;

    private PocketTeleportPreparation telePrep;

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && telePrep != null)
            if (telePrep.doPrepareTick())
                telePrep = null;
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        if (worldObj.isRemote)
            return;

        Utils.spawnItemStack(generateItemStack(), worldObj, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, 0);
        unloadPocket();
    }

    @Override
    public void onNeighbourBlockChanged(World world, int x, int y, int z, Block block) {
        // do nothing
    }

    @Override
    public void onNeighbourTileChanged(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        // do nothing
    }

    @Override
    public void onBlockAdded(EntityLivingBase entityLivingBase, World world, int x, int y, int z, ItemStack itemStack) {
        if (worldObj.isRemote)
            return;

        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemCompound = itemStack.getTagCompound();

            CoordSet chunkSet = CoordSet.readFromNBT(itemCompound);
            boolean success = PocketRegistry.getPocket(chunkSet) != null;

            if (!success)
                throw new RuntimeException("YOU DESERVED THIS!");

            PocketRegistry.updatePocket(chunkSet, entityLivingBase.dimension, getCoordSet());

            if (itemCompound.hasKey("display")) {
                String tempString = itemCompound.getCompoundTag("display").getString("Name");
                if (!tempString.isEmpty())
                    customName = tempString;
            }
        }

        Pocket pocket = getPocket();
        pocket.generatePocketRoom();

        ChunkLoaderHandler.addPocketToChunkLoader(pocket);
    }

    @Override
    public boolean onActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ) {
        if (player == null)
            return true;
        
        ItemStack equippedItemStack = player.getCurrentEquippedItem();
        if (equippedItemStack != null) {
            if (Utils.isItemPocketWrench(equippedItemStack)) {
                new NetherCrystalHandler().onItemUseFirst(equippedItemStack, player, world, getCoordSet(), side, hitVecX, hitVecY, hitVecZ);
            }
            return true;
        }

        prepareTeleportIntoPocket(player);
        return true;
    }

    public ItemStack generateItemStack() {
        ItemStack itemStack = new ItemStack(ModBlocks.dimensionalPocket);

        if (!itemStack.hasTagCompound())
            itemStack.setTagCompound(new NBTTagCompound());

        CoordSet chunkSet = getPocket().getChunkCoords();
        chunkSet.writeToNBT(itemStack.getTagCompound());

        int id = chunkSet.getX() * 16 + chunkSet.getY() + 1;

        itemStack = Utils.generateItem(itemStack, customName, false, "~ Pocket " + id + " ~");
        return itemStack;
    }

    public void unloadPocket() {
        ChunkLoaderHandler.removePocketFromChunkLoader(getPocket());
    }

    @Override
    public Pocket getPocket() {
        if (worldObj.isRemote)
            return pocket;
        
        return PocketRegistry.getOrCreatePocket(worldObj, getCoordSet());
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        getPocket().writeToNBT(tag);
        if (customName != null)
            tag.setString(TAG_CUSTOM_DP_NAME, customName);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        
        if (worldObj != null && worldObj.isRemote) // worldObj is null on initial world loading
            pocket = Pocket.readFromNBT(tag);;
        
        String tempString = tag.getString(TAG_CUSTOM_DP_NAME);
        if (!tempString.isEmpty())
            customName = tempString;
    }

    public void prepareTeleportIntoPocket(EntityPlayer player) {
        int ticksToTake = 15;
        if (!worldObj.isRemote) {
            telePrep = new PocketTeleportPreparation(player, ticksToTake, getPocket(), Direction.INTO_POCKET);
        } else {
            // TODO Sync for all clients.
            for (int i = 0; i < 40; i++)
                UtilsFX.createPlayerStream(player, getCoordSet(), ticksToTake);
        }
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        Pocket p = getPocket();
        if (p == null) return false;

        switch (p.getFlowState(from)) {
            case ENERGY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the neighboring TileEntity of the Frame connector block at the given side.
     *
     * @param side
     * @return the neighboring TE, or null if the the chunk is not loaded or no TE exists at the spot.
     */
    private TileEntity getFrameConnectorNeighborTileEntity(ForgeDirection side) {
        Pocket p = getPocket();
        if (p == null) return null;

        World targetWorld = MinecraftServer.getServer().worldServerForDimension(Reference.DIMENSION_ID);

        CoordSet targetCoords = p.getConnectorCoords(side);
        if (targetCoords == null || !targetWorld.blockExists(targetCoords.getX(), targetCoords.getY(), targetCoords.getZ())) {
            return null;
        }

        targetCoords.addForgeDirection(side.getOpposite());
        if (!targetWorld.blockExists(targetCoords.getX(), targetCoords.getY(), targetCoords.getZ())) {
            return null;
        }

        return targetWorld.getTileEntity(targetCoords.getX(), targetCoords.getY(), targetCoords.getZ());
    }

    /**
     * Redirects the receiveEnergy call to the neighboring TileEntity of the corresponding FrameConnector wall block if possible.
     */
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        TileEntity targetTE = getFrameConnectorNeighborTileEntity(from);

        if (targetTE instanceof IEnergyReceiver) {
            int received = ((IEnergyReceiver) targetTE).receiveEnergy(from, maxReceive, simulate);
            return received;
        }

        return 0;
    }

    /**
     * Redirects the extractEnergy call to the neighboring TileEntity of the corresponding FrameConnector wall block if possible.
     */
    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        TileEntity targetTE = getFrameConnectorNeighborTileEntity(from);

        if (targetTE instanceof IEnergyProvider) {
            int extracted = ((IEnergyProvider) targetTE).extractEnergy(from, maxExtract, simulate);
            return extracted;
        }

        return 0;
    }

    /**
     * Redirects the getEnergyStored call to the neighboring TileEntity of the corresponding FrameConnector wall block if possible.
     */
    @Override
    public int getEnergyStored(ForgeDirection from) {
        TileEntity targetTE = getFrameConnectorNeighborTileEntity(from);

        if (targetTE instanceof IEnergyProvider) {
            int stored = ((IEnergyProvider) targetTE).getEnergyStored(from);
            return stored;
        } else if (targetTE instanceof IEnergyReceiver) {
            int stored = ((IEnergyReceiver) targetTE).getEnergyStored(from);
            return stored;
        }

        return 0;
    }

    /**
     * Redirects the getMaxEnergyStored call to the neighboring TileEntity of the corresponding FrameConnector wall block if possible.
     */
    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        TileEntity targetTE = getFrameConnectorNeighborTileEntity(from);

        if (targetTE instanceof IEnergyProvider) {
            int maxStored = ((IEnergyProvider) targetTE).getMaxEnergyStored(from);
            return maxStored;
        } else if (targetTE instanceof IEnergyReceiver) {
            int maxStored = ((IEnergyReceiver) targetTE).getMaxEnergyStored(from);
            return maxStored;
        }

        return 0;
    }
}
