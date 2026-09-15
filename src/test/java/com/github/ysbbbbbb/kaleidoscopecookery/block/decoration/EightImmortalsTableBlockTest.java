package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EightImmortalsTableBlockTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @EnumSource(value = Direction.class, names = {"NORTH", "SOUTH", "EAST", "WEST"})
    void everyPartResolvesToTheSameAnchorAcrossChunkBoundaries(Direction facing) {
        for (BlockPos anchor : new BlockPos[]{new BlockPos(15, 64, 15), new BlockPos(-16, -60, -16)}) {
            Set<BlockPos> positions = new HashSet<>();
            for (EightImmortalsTableBlock.Part part : EightImmortalsTableBlock.Part.values()) {
                BlockPos position = EightImmortalsTableBlock.getPartPos(anchor, facing, part);
                assertTrue(positions.add(position));
                assertEquals(anchor, EightImmortalsTableBlock.getAnchorPos(position, facing, part));
                assertEquals(anchor.getY(), position.getY());
            }
            assertEquals(4, positions.size());
            assertTrue(positions.contains(anchor));
            assertTrue(positions.contains(anchor.relative(facing)));
            assertTrue(positions.contains(anchor.relative(facing.getCounterClockWise())));
            assertTrue(positions.contains(anchor.relative(facing).relative(facing.getCounterClockWise())));
        }
    }
}
