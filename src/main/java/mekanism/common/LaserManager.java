package mekanism.common;

import java.util.List;
import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.event.world.BlockEvent;

public class LaserManager {

    public static LaserInfo fireLaser(TileEntity from, Direction direction, double energy, World world) {
        return fireLaser(new Pos3D(from).centre().translate(direction, 0.501), direction, energy, world);
    }

    public static LaserInfo fireLaser(Pos3D from, Direction direction, double energy, World world) {
        Pos3D to = from.clone().translate(direction, MekanismConfig.current().general.laserRange.val() - 0.002);
        BlockRayTraceResult mop = world.rayTraceBlocks(from, to);
        if (mop != null) {
            to = new Pos3D(mop.getHitVec());
            Coord4D toCoord = new Coord4D(mop.getPos(), world);
            TileEntity tile = toCoord.getTileEntity(world);
            CapabilityUtils.getCapabilityHelper(tile, Capabilities.LASER_RECEPTOR_CAPABILITY, mop.getFace()).ifPresent(receptor -> {
                if (!receptor.canLasersDig()) {
                    receptor.receiveLaserEnergy(energy, mop.getFace());
                }
            });
        }
        from.translateExcludingSide(direction, -0.1);
        to.translateExcludingSide(direction, 0.1);

        boolean foundEntity = false;
        for (Entity e : world.getEntitiesWithinAABB(Entity.class, Pos3D.getAABB(from, to))) {
            foundEntity = true;
            if (!e.isImmuneToFire()) {
                e.setFire((int) (energy / 1000));
            }
            if (energy > 256) {
                e.attackEntityFrom(DamageSource.GENERIC, (float) energy / 1000F);
            }
        }
        return new LaserInfo(mop, foundEntity);
    }

    public static List<ItemStack> breakBlock(Coord4D blockCoord, boolean dropAtBlock, World world, BlockPos laserPos) {
        if (!MekanismConfig.current().general.aestheticWorldDamage.val()) {
            return null;
        }

        BlockState state = blockCoord.getBlockState(world);
        Block blockHit = state.getBlock();
        PlayerEntity dummy = Mekanism.proxy.getDummyPlayer((ServerWorld) world, laserPos).get();
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, blockCoord.getPos(), state, dummy);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return null;
        }
        NonNullList<ItemStack> ret = null;
        if (dropAtBlock) {
            Block.spawnDrops(state, world, blockCoord.getPos(), world.getTileEntity(blockCoord.getPos()));
        } else {
            ret = NonNullList.create();
            blockHit.getDrops(ret, world, blockCoord.getPos(), state, 0);
        }
        blockHit.breakBlock(world, blockCoord.getPos(), state);
        world.removeBlock(blockCoord.getPos(), false);
        world.playEvent(WorldEvents.BREAK_BLOCK_EFFECTS, blockCoord.getPos(), Block.getStateId(state));
        return ret;
    }

    public static BlockRayTraceResult fireLaserClient(TileEntity from, Direction direction, double energy, World world) {
        return fireLaserClient(new Pos3D(from).centre().translate(direction, 0.501), direction, energy, world);
    }

    public static BlockRayTraceResult fireLaserClient(Pos3D from, Direction direction, double energy, World world) {
        Pos3D to = from.clone().translate(direction, MekanismConfig.current().general.laserRange.val() - 0.002);
        BlockRayTraceResult mop = world.rayTraceBlocks(from, to);
        if (mop != null) {
            to = new Pos3D(mop.getHitVec());
        }
        from.translate(direction, -0.501);
        Mekanism.proxy.renderLaser(world, from, to, direction, energy);
        return mop;
    }

    //TODO: Should this be removed?
    public static boolean isReceptor(TileEntity tile, Direction side) {
        return CapabilityUtils.getCapabilityHelper(tile, Capabilities.LASER_RECEPTOR_CAPABILITY, side).isPresent();
    }

    public static class LaserInfo {

        public BlockRayTraceResult movingPos;

        public boolean foundEntity;

        public LaserInfo(BlockRayTraceResult mop, boolean b) {
            movingPos = mop;
            foundEntity = b;
        }
    }
}