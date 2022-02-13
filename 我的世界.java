public class ExtendedBlockStorage

{undefined

/** Contains the bottom-most Y block represented by this ExtendedBlockStorage. Typically a multiple of 16. */

private int yBase;

/** A total count of the number of non-air blocks in this block storage's Chunk. */

private int blockRefCount;

/**

* Contains the number of blocks in this block storage's parent chunk that require random ticking. Used to cull the

* Chunk from random tick updates for performance reasons.

*/

private int tickRefCount;

private char[] data;

/** The NibbleArray containing a block of Block-light data. */

private NibbleArray blocklightArray;

/** The NibbleArray containing a block of Sky-light data. */

private NibbleArray skylightArray;

public ExtendedBlockStorage(int y, boolean storeSkylight)

{undefined

this.yBase = y;

this.data = new char[4096];

this.blocklightArray = new NibbleArray();

if (storeSkylight)

{undefined

this.skylightArray = new NibbleArray();

}

}

public IBlockState get(int x, int y, int z)

{undefined

IBlockState iblockstate = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);

return iblockstate != null ? iblockstate : Blocks.air.getDefaultState();

}

public void set(int x, int y, int z, IBlockState state)

{undefined

if (state instanceof net.minecraftforge.common.property.IExtendedBlockState)

state = ((net.minecraftforge.common.property.IExtendedBlockState) state).getClean();

IBlockState iblockstate1 = this.get(x, y, z);

Block block = iblockstate1.getBlock();

Block block1 = state.getBlock();

if (block != Blocks.air)

{undefined

--this.blockRefCount;

if (block.getTickRandomly())

{undefined

--this.tickRefCount;

}

}

if (block1 != Blocks.air)

{undefined

++this.blockRefCount;

if (block1.getTickRandomly())

{undefined

++this.tickRefCount;

}

}

this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);

}

/**

* Returns the block for a location in a chunk, with the extended ID merged from a byte array and a NibbleArray to

* form a full 12-bit block ID.

*/

public Block getBlockByExtId(int x, int y, int z)

{undefined

return this.get(x, y, z).getBlock();

}

/**

* Returns the metadata associated with the block at the given coordinates in this ExtendedBlockStorage.

*/

public int getExtBlockMetadata(int x, int y, int z)

{undefined

IBlockState iblockstate = this.get(x, y, z);

return iblockstate.getBlock().getMetaFromState(iblockstate);

}

/**

* Returns whether or not this block storage's Chunk is fully empty, based on its internal reference count.

*/

public boolean isEmpty()

{undefined

return this.blockRefCount == 0;

}

/**

* Returns whether or not this block storage's Chunk will require random ticking, used to avoid looping through

* random block ticks when there are no blocks that would randomly tick.

*/

public boolean getNeedsRandomTick()

{undefined

return this.tickRefCount > 0;

}

/**

* Returns the Y location of this ExtendedBlockStorage.

*/

public int getYLocation()

{undefined

return this.yBase;

}

/**

* Sets the saved Sky-light value in the extended block storage structure.

*/

public void setExtSkylightValue(int x, int y, int z, int value)

{undefined

this.skylightArray.set(x, y, z, value);

}

/**

* Gets the saved Sky-light value in the extended block storage structure.

*/

public int getExtSkylightValue(int x, int y, int z)

{undefined

return this.skylightArray.get(x, y, z);

}

/**

* Sets the saved Block-light value in the extended block storage structure.

*/

public void setExtBlocklightValue(int x, int y, int z, int value)

{undefined

this.blocklightArray.set(x, y, z, value);

}

/**

* Gets the saved Block-light value in the extended block storage structure.

*/

public int getExtBlocklightValue(int x, int y, int z)

{undefined

return this.blocklightArray.get(x, y, z);

}

public void removeInvalidBlocks()

{undefined

this.blockRefCount = 0;

this.tickRefCount = 0;

for (int i = 0; i < 16; ++i)

{undefined

for (int j = 0; j < 16; ++j)

{undefined

for (int k = 0; k < 16; ++k)

{undefined

Block block = this.getBlockByExtId(i, j, k);

if (block != Blocks.air)

{undefined

++this.blockRefCount;

if (block.getTickRandomly())

{undefined

++this.tickRefCount;

}

}

}

}

}

}

public char[] getData()

{undefined

return this.data;

}

public void setData(char[] dataArray)

{undefined

this.data = dataArray;

}

/**

* Returns the NibbleArray instance containing Block-light data.

*/

public NibbleArray getBlocklightArray()

{undefined

return this.blocklightArray;

}

/**

* Returns the NibbleArray instance containing Sky-light data.

*/

public NibbleArray getSkylightArray()

{undefined

return this.skylightArray;

}

/**

* Sets the NibbleArray instance used for Block-light values in this particular storage block.

*/

public void setBlocklightArray(NibbleArray newBlocklightArray)

{undefined

this.blocklightArray = newBlocklightArray;

}

/**

* Sets the NibbleArray instance used for Sky-light values in this particular storage block.

*/

public void setSkylightArray(NibbleArray newSkylightArray)

{undefined

this.skylightArray = newSkylightArray;

}

}

卸载对应的函数是Chunk.onChunkUnload

/**

* Called when this Chunk is unloaded by the ChunkProvider

*/

public void onChunkUnload()

{undefined

this.isChunkLoaded = false;

Iterator iterator = this.chunkTileEntityMap.values().iterator();

while (iterator.hasNext())

{undefined

TileEntity tileentity = (TileEntity)iterator.next();

this.worldObj.markTileEntityForRemoval(tileentity);

}

for (int i = 0; i < this.entityLists.length; ++i)

{undefined

this.worldObj.unloadEntities(this.entityLists[i]);

}

MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(this));

}

初次生成世界，需要预先加载一部分的块，对应的是Minecraft Server的

protected void initialWorldChunkLoad()

{undefined

boolean flag = true;

boolean flag1 = true;

boolean flag2 = true;

boolean flag3 = true;

int i = 0;

this.setUserMessage("menu.generatingTerrain");

byte b0 = 0;

logger.info("Preparing start region for level " + b0);

WorldServer worldserver = net.minecraftforge.common.DimensionManager.getWorld(b0);

BlockPos blockpos = worldserver.getSpawnPoint();

long j = getCurrentTimeMillis();

for (int k = -192; k <= 192 && this.isServerRunning(); k += 16)

{undefined

for (int l = -192; l <= 192 && this.isServerRunning(); l += 16)

{undefined

long i1 = getCurrentTimeMillis();

if (i1 - j > 1000L)

{undefined

this.outputPercentRemaining("Preparing spawn area", i * 100 / 625);

j = i1;

}

++i;

worldserver.theChunkProviderServer.loadChunk(blockpos.getX() + k >> 4, blockpos.getZ() + l >> 4);

}

}

this.clearCurrentTask();

}

对应PlayerInstance中的

public void addPlayer(EntityPlayerMP playerMP)

{undefined

if (this.playersWatchingChunk.contains(playerMP))

{undefined

PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", new Object[] {playerMP, Integer.valueOf(this.chunkCoords.chunkXPos), Integer.valueOf(this.chunkCoords.chunkZPos)});

}

else

{undefined

if (this.playersWatchingChunk.isEmpty())

{undefined

this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();

}

this.playersWatchingChunk.add(playerMP);

Runnable playerRunnable = null;

if (this.loaded)

{undefined

playerMP.loadedChunks.add(this.chunkCoords);

}

else

{undefined

final EntityPlayerMP tmp = playerMP;

playerRunnable = new Runnable()

{undefined

public void run()

{undefined

tmp.loadedChunks.add(PlayerInstance.this.chunkCoords);

}

};

PlayerManager.this.getMinecraftServer().theChunkProviderServer.loadChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos, playerRunnable);

}

this.players.put(playerMP, playerRunnable);

}

Chunk加载对应的回调函数

/**

* Called when this Chunk is loaded by the ChunkProvider

*/

public void onChunkLoad()

{undefined

this.isChunkLoaded = true;

this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

for (int i = 0; i < this.entityLists.length; ++i)

{undefined

Iterator iterator = this.entityLists[i].iterator();

while (iterator.hasNext())

{undefined

Entity entity = (Entity)iterator.next();

entity.onChunkLoad();

}

this.worldObj.loadEntities(com.google.common.collect.ImmutableList.copyOf(this.entityLists[i]));

}

MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(this));

}
