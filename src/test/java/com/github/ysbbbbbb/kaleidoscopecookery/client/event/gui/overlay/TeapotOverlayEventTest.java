package com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeapotOverlayEventTest {
    @Test
    void keepsOriginalPositionWithoutActionBar() {
        assertEquals(168, TeapotOverlayEvent.getOverlayTop(240, 9, false, false));
        assertEquals(168, TeapotOverlayEvent.getOverlayTop(240, 9, true, false));
    }

    @Test
    void entirePanelStaysAboveActionBar() {
        for (int screenHeight : new int[]{240, 360, 540, 1080}) {
            for (int lineHeight : new int[]{9, 12, 16}) {
                for (boolean hasDetail : new boolean[]{false, true}) {
                    int top = TeapotOverlayEvent.getOverlayTop(screenHeight, lineHeight, hasDetail, true);
                    int panelBottom = top + lineHeight + (hasDetail ? lineHeight + 2 : 0);
                    int actionBarTop = screenHeight - 72;
                    assertTrue(top >= 0);
                    assertEquals(4, actionBarTop - panelBottom);
                }
            }
        }
    }
}
