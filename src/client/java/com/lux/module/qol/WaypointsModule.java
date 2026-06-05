package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.FloatSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class WaypointsModule extends Module {

    public static class Waypoint {
        public String name; public double x, y, z; public int color;
        public Waypoint(String n, double x, double y, double z, int c) {
            this.name = n; this.x = x; this.y = y; this.z = z; this.color = c;
        }
    }

    private final List<Waypoint> waypoints = new ArrayList<>();
    private BooleanSetting showDistance;
    private FloatSetting maxDistance;

    public WaypointsModule() { super("Waypoints", "Mark locations with named beacons", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        showDistance = register(new BooleanSetting("Show Distance", "Show distance to waypoint", true));
        maxDistance  = register(new FloatSetting("Max Distance", "Max render distance in blocks", 1000f, 50f, 5000f));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        ClientPlayerEntity p = mc.player;
        if (p == null || waypoints.isEmpty()) return;
        int x = getHudX(), y = getHudY();
        ctx.drawTextWithShadow(mc.textRenderer, "Waypoints:", x, y, 0xFFFFFFFF);
        y += 12;
        for (Waypoint wp : waypoints) {
            double dist = p.getPos().distanceTo(new Vec3d(wp.x, wp.y, wp.z));
            if (dist > maxDistance.getValue()) continue;
            String text = wp.name + (showDistance.getValue() ? " (" + (int)dist + "m)" : "");
            ctx.drawTextWithShadow(mc.textRenderer, text, x, y, wp.color);
            y += 10;
        }
    }

    public void addWaypoint(Waypoint wp)    { waypoints.add(wp); }
    public void removeWaypoint(String name) { waypoints.removeIf(w -> w.name.equals(name)); }
    public List<Waypoint> getWaypoints()    { return waypoints; }
}
