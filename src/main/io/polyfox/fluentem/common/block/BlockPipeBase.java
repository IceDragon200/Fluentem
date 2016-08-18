/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2016 IceDragon200
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.polyfox.fluentem.common.block;

import java.util.List;
import java.util.Random;

import growthcraft.api.core.util.BoundUtils;
import growthcraft.api.core.util.GrcColorPreset;
import growthcraft.core.common.block.GrcBlockBase;
import growthcraft.core.common.block.IDroppableBlock;
import growthcraft.core.common.block.IWrenchable;
import growthcraft.core.util.ItemUtils;
import io.polyfox.fluentem.client.renderer.RenderPipe;
import io.polyfox.fluentem.common.tileentity.TileEntityPipeBase;
import io.polyfox.fluentem.Fluentem;
import io.polyfox.fluentem.util.PipeConsts;
import io.polyfox.fluentem.util.PipeType;

import buildcraft.api.blocks.IColorRemovable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPipeBase extends GrcBlockBase implements IPipeBlock, ITileEntityProvider, IDroppableBlock, IWrenchable, IColorRemovable
{
	public final float[] bounds = new float[6];
	protected final float unit = 1.0f / 32.0f;
	protected final float[] unitBounds = new float[] { -unit, -unit, -unit, unit, unit, unit };
	private PipeType pipeType;

	public BlockPipeBase(PipeType type)
	{
		super(Material.glass);
		setHardness(0.1F);
		setStepSound(soundTypeGlass);
		setBlockName("grc.pipeBase");
		setCreativeTab(Fluentem.creativeTab);
		this.pipeType = type;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubBlocks(Item block, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(block, 1, GrcColorPreset.Transparent.ordinal()));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			world.notifyBlocksOfNeighborChange(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, this);
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	public PipeType getPipeType()
	{
		return pipeType;
	}

	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityPipeBase();
	}

	public TileEntityPipeBase getTileEntity(IBlockAccess world, int x, int y, int z)
	{
		final TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityPipeBase)
		{
			return (TileEntityPipeBase)te;
		}
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		final TileEntityPipeBase pipeBase = getTileEntity(world, x, y, z);
		if (pipeBase != null) pipeBase.onNeighbourChanged();
	}

	@Override
	public int getRenderType()
	{
		return RenderPipe.RENDER_ID;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}

	public boolean setBlockColour(World world, int x, int y, int z, ForgeDirection side, GrcColorPreset colour)
	{
		final TileEntityPipeBase te = getTileEntity(world, x, y, z);

		if (te != null)
		{
			te.setColour(colour);
			return true;
		}
		return false;
	}

	/* IColorRemovable */
	@Override
	public boolean removeColorFromBlock(World world, int x, int y, int z, ForgeDirection side)
	{
		return setBlockColour(world, x, y, z, side, GrcColorPreset.Transparent);
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		return setBlockColour(world, x, y, z, side, GrcColorPreset.VALID_COLORS.get(colour));
	}

	public boolean wrenchBlock(World world, int x, int y, int z, EntityPlayer player, ItemStack wrench)
	{
		if (player != null)
		{
			final ItemStack is = player.inventory.getCurrentItem();
			if (ItemUtils.canWrench(is, player, x, y, z))
			{
				/*
				 * This branch is for removing the pipe using a wrench, while the
				 * player is sneaking.
				 */
				if (player.isSneaking())
				{
					if (!world.isRemote)
					{
						fellBlockAsItem(world, x, y, z);
						ItemUtils.wrenchUsed(is, player, x, y, z);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z)
	{
		final TileEntityPipeBase te = getTileEntity(world, x, y, z);
		if (te != null)
		{
			return te.getColour().ordinal();
		}
		return GrcColorPreset.Transparent.ordinal();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		setBlockColour(world, x, y, z, ForgeDirection.UNKNOWN, GrcColorPreset.toColour(itemstack.getItemDamage()));
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		final TileEntityPipeBase pipe = getTileEntity(world, x, y, z);
		if (pipe != null)
		{
			pipe.refreshCache();
		}
	}

	private void expandBoundsToFit(float[] expander)
	{
		for (int side = 0; side < 3; ++side)
		{
			if (expander[side] < bounds[side]) bounds[side] = expander[side];
		}
		for (int side = 3; side < 6; ++side)
		{
			if (expander[side] > bounds[side]) bounds[side] = expander[side];
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		final TileEntityPipeBase te = getTileEntity(world, x, y, z);
		if (te != null)
		{
			setBlockBounds(te.bounds[0], te.bounds[1], te.bounds[2], te.bounds[3], te.bounds[4], te.bounds[5]);
		}
		else
		{
			bounds[0] = 1f;
			bounds[1] = 1f;
			bounds[2] = 1f;
			bounds[3] = -1f;
			bounds[4] = -1f;
			bounds[5] = -1f;
			switch (pipeType)
			{
				case BASE:
					expandBoundsToFit(PipeConsts.PIPE_BASE_CORE);
					break;
				case VACUUM:
					expandBoundsToFit(PipeConsts.PIPE_VACUUM_CORE);
					break;
				default:
			}
			setBlockBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		bounds[0] = (float)minX;
		bounds[1] = (float)minY;
		bounds[2] = (float)minZ;
		bounds[3] = (float)maxX;
		bounds[4] = (float)maxY;
		bounds[5] = (float)maxZ;
		BoundUtils.addBounds(bounds, unitBounds);
		BoundUtils.clampBounds(bounds);
		setBlockBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}
}
