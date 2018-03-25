package org.neo4j.io.nvmfs;

public class NvmStoreFileChannelUnwrapper {

    public static NvmStoreFileChannel unwrap( NvmStoreChannel channel )
    {
        return NvmStoreFileChannel.unwrap( channel );
    }

}
