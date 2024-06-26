// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Rendering target rendering to tessellator
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.renderers;

import static gcewing.sg.utils.BaseUtils.ifloor;
import static gcewing.sg.utils.BaseUtils.iround;
import static java.lang.Math.floor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3d;
import org.joml.Vector3i;

import gcewing.sg.SGCraft;
import gcewing.sg.utils.Trans3;

public class BaseWorldRenderTarget extends BaseRenderTarget {

    protected IBlockAccess world;
    protected Vector3i blockPos;
    protected Block block;
    protected Tessellator tess;
    protected float cmr = 1, cmg = 1, cmb = 1;
    protected boolean ao;
    protected boolean axisAlignedNormal;
    protected boolean renderingOccurred;
    protected float vr, vg, vb, va; // Colour to be applied to next vertex
    protected int vlm1, vlm2; // Light map values to be applied to next vertex

    public BaseWorldRenderTarget(IBlockAccess world, Vector3i pos, Tessellator tess, IIcon overrideIcon) {
        super(pos.x, pos.y, pos.z, overrideIcon);
        this.world = world;
        this.blockPos = pos;
        this.block = world.getBlock(pos.x, pos.y, pos.z);
        this.tess = tess;
        ao = Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0;
        expandTrianglesToQuads = true;
    }

    @Override
    public void setNormal(Vector3d n) {
        super.setNormal(n);
        Vector3d vector = Trans3.getDirectionVec(face);

        axisAlignedNormal = n.dot(vector) >= 0.99;
    }

    protected void rawAddVertex(Vector3d p, double u, double v) {
        lightVertex(p);
        tess.setColorRGBA_F(vr, vg, vb, va);
        tess.setTextureUV(u, v);
        tess.setBrightness((vlm1 << 16) | vlm2);
        tess.addVertex(p.x, p.y, p.z);
        renderingOccurred = true;
    }

    protected void lightVertex(Vector3d p) {
        // TODO: Colour multiplier
        if (ao) aoLightVertex(p);
        else brLightVertex(p);
    }

    protected void aoLightVertex(Vector3d v) {
        Vector3d n = normal;
        double brSum1 = 0, brSum2 = 0, lvSum = 0, wt = 0;
        // Sample a unit cube offset half a block in the direction of the normal
        double vx = v.x + 0.5 * n.x;
        double vy = v.y + 0.5 * n.y;
        double vz = v.z + 0.5 * n.z;
        // Examine 8 neighbouring blocks
        for (int dx = -1; dx <= 1; dx += 2) for (int dy = -1; dy <= 1; dy += 2) for (int dz = -1; dz <= 1; dz += 2) {
            int X = ifloor(vx + 0.5 * dx);
            int Y = ifloor(vy + 0.5 * dy);
            int Z = ifloor(vz + 0.5 * dz);
            Vector3i pos = new Vector3i(X, Y, Z);
            // Calculate overlap of sampled block with sampling cube
            double wox = (dx < 0) ? (X + 1) - (vx - 0.5) : (vx + 0.5) - X;
            double woy = (dy < 0) ? (Y + 1) - (vy - 0.5) : (vy + 0.5) - Y;
            double woz = (dz < 0) ? (Z + 1) - (vz - 0.5) : (vz + 0.5) - Z;
            // Take weighted sample of brightness and light value
            double w = wox * woy * woz;
            if (w <= 0) {
                continue;
            }

            int br;
            try {
                br = block.getMixedBrightnessForBlock(world, pos.x, pos.y, pos.z);
            } catch (RuntimeException e) {
                SGCraft.log.error(
                        String.format(
                                "BaseWorldRenderTarget.aoLightVertex: getMixedBrightnessForBlock(%s) with weight %s for block at %s: %s",
                                pos,
                                w,
                                blockPos,
                                e));
                SGCraft.log.error(String.format("BaseWorldRenderTarget.aoLightVertex: v = %s n = %s", v, n));
                throw e;
            }
            float lv;
            if (!pos.equals(blockPos)) lv = world.getBlock(pos.x, pos.y, pos.z).getAmbientOcclusionLightValue();
            else lv = 1.0f;
            if (br != 0) {
                double br1 = ((br >> 16) & 0xff) / 240.0;
                double br2 = (br & 0xff) / 240.0;
                brSum1 += w * br1;
                brSum2 += w * br2;
                wt += w;
            }
            lvSum += w * lv;
        }
        int brv;
        if (wt > 0) brv = (iround(brSum1 / wt * 0xf0) << 16) | iround(brSum2 / wt * 0xf0);
        else brv = block.getMixedBrightnessForBlock(world, blockPos.x, blockPos.y, blockPos.z);
        float lvv = (float) lvSum;
        setLight(shade * lvv, brv);
    }

    protected void brLightVertex(Vector3d p) {
        Vector3d n = normal;
        Vector3i pos;
        if (axisAlignedNormal) {
            pos = new Vector3i(
                    (int) floor(p.x + 0.01 * n.x),
                    (int) floor(p.y + 0.01 * n.y),
                    (int) floor(p.z + 0.01 * n.z));
        } else pos = blockPos;
        int br = block.getMixedBrightnessForBlock(world, pos.x, pos.y, pos.z);
        setLight(shade, br);
    }

    protected void setLight(float shadow, int br) {
        vr = shadow * cmr * r();
        vg = shadow * cmg * g();
        vb = shadow * cmb * b();
        va = a();
        vlm1 = br >> 16;
        vlm2 = br & 0xffff;
    }

    public boolean end() {
        super.finish();
        return renderingOccurred;
    }

    public void setRenderingOccurred() {
        renderingOccurred = true;
    }

}
