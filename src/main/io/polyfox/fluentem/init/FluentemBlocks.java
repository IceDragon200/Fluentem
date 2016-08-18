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
package io.polyfox.fluentem.init;

import growthcraft.core.common.definition.BlockTypeDefinition;
import growthcraft.core.common.GrcModuleBlocks;
import io.polyfox.fluentem.common.block.BlockPipeBase;
import io.polyfox.fluentem.common.item.ItemBlockPipeBase;
import io.polyfox.fluentem.common.tileentity.TileEntityPipeBase;
import io.polyfox.fluentem.util.PipeType;

import cpw.mods.fml.common.registry.GameRegistry;

public class FluentemBlocks extends GrcModuleBlocks
{
	public BlockTypeDefinition<BlockPipeBase> pipeBase;
	public BlockTypeDefinition<BlockPipeBase> pipeVacuum;

	@Override
	public void preInit()
	{
		pipeBase = newTypedDefinition(new BlockPipeBase(PipeType.BASE));
		pipeBase.getBlock().setBlockName("fluentem.pipe_base");
		pipeVacuum = newTypedDefinition(new BlockPipeBase(PipeType.VACUUM));
		pipeVacuum.getBlock().setBlockName("fluentem.pipe_vacuum");
	}

	@Override
	public void register()
	{
		pipeBase.register("fluentem.pipe_base", ItemBlockPipeBase.class);
		pipeVacuum.register("fluentem.pipe_vacuum", ItemBlockPipeBase.class);
		GameRegistry.registerTileEntity(TileEntityPipeBase.class, "fluentem.tileentity.pipe_base");
	}
}
