package dev.sebastianb.stardriven.client.render.dimension.star;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Random;

public class GalaxyStarRendererManager {

    static class GalaxyStar {
        double size;
        double rotation;

        int x;
        int y;
        int z;

        public GalaxyStar(int x, int y, int z, double size, double rotation) {
            this.x = x;
            this.y = y;
            this.z = z;

            this.size = size;
            this.rotation = rotation;

        }

        // Method to compute dot product with another vector
        public double dotProduct(Vec3d other) {
            Vec3d starVector = new Vec3d(x, y, z);
            return starVector.dotProduct(other);
        }

    }


    // TODO: implement star chunk rendering system of sorts after array is figured out
    HashSet<GalaxyStar> galaxyStars = new HashSet<>();

    private static DimensionalStarPosition cameraRenderPosition = new DimensionalStarPosition(0,0,0); // starts in default position

    private VertexBuffer starBuffer;

    public GalaxyStarRendererManager() {
        setStarPositions();
        setRelativeCameraRenderPosition(cameraRenderPosition); // sets the camera position to the default
    }

    public void setStarPositions() {
        final Random random = new Random(27893L);

        for (int i = 0; i < 20000; i++) {
            int size = 850;

            int x = random.nextInt((size * 2) + 1) - size;  // Generates random int between -1000 and 1000
            int y = random.nextInt((size * 2) + 1) - size;  // Generates random int between -1000 and 1000
            int z = random.nextInt((size * 2) + 1) - size;  // Generates random int between -1000 and 1000

            galaxyStars.add(new GalaxyStar(x, y, z,
                    random.nextFloat(0.3f) + 1,
                    getRandomRotation(random)
                    ));
        }

    }

    private double getRandomRotation(Random random) {
        return random.nextDouble(360) + 1;
    }

    public void setupBufferPositions() {
        if (this.starBuffer == null) {
            this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }



        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.enableBlend();

        RenderSystem.disableCull();


        // Calculate the direction from the star to the camera
        Vec3d cameraPos = new Vec3d(cameraRenderPosition.getX(), cameraRenderPosition.getY(), cameraRenderPosition.getZ());


        for (GalaxyStar star : galaxyStars) {

            double starSize = star.size;


            double x = star.x - cameraRenderPosition.getX();
            double y = star.y - cameraRenderPosition.getY();
            double z = star.z - cameraRenderPosition.getZ();

            Vec3d starToCamera = new Vec3d(x, y, z); // Star's position relative to camera

            Vec3d starToCameraNormalized = starToCamera.normalize();

            var starPos = new Vec3d(star.x, star.y, star.z);

            double distance = starPos.distanceTo(cameraPos);

            double modifiedStarSize = getModifiedStarSize(distance, starSize);

            if (modifiedStarSize == 0) {
                continue;
            }

            double distanceAndSizeMultiplied = distance * modifiedStarSize * 1.23;


            Vec3d right = starToCameraNormalized.crossProduct(new Vec3d(0, 1, 0)).normalize();

            // Compute the up vector using cross product of right and starToCamera
            Vec3d up = right.crossProduct(starToCameraNormalized).normalize();


            // size of a star to the camera
            double halfSize = (getModifiedStarSize(distanceAndSizeMultiplied, starSize) / 2.0D);


            double angle = Math.toRadians(star.rotation);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // Compute rotated right and up vectors
            Vec3d rotatedRight = new Vec3d(
                    cos * right.x - sin * up.x,
                    cos * right.y - sin * up.y,
                    cos * right.z - sin * up.z
            ).normalize();

            Vec3d rotatedUp = new Vec3d(
                    sin * right.x + cos * up.x,
                    sin * right.y + cos * up.y,
                    sin * right.z + cos * up.z
            ).normalize();

            // Define vertices of the quad (billboard) around the star with rotation
            buffer.vertex(
                    (float) (starToCamera.x - rotatedRight.x * halfSize - rotatedUp.x * halfSize),
                    (float) (starToCamera.y - rotatedRight.y * halfSize - rotatedUp.y * halfSize),
                    (float) (starToCamera.z - rotatedRight.z * halfSize - rotatedUp.z * halfSize)
            );

            buffer.vertex(
                    (float) (starToCamera.x + rotatedRight.x * halfSize - rotatedUp.x * halfSize),
                    (float) (starToCamera.y + rotatedRight.y * halfSize - rotatedUp.y * halfSize),
                    (float) (starToCamera.z + rotatedRight.z * halfSize - rotatedUp.z * halfSize)
            );

            buffer.vertex(
                    (float) (starToCamera.x + rotatedRight.x * halfSize + rotatedUp.x * halfSize),
                    (float) (starToCamera.y + rotatedRight.y * halfSize + rotatedUp.y * halfSize),
                    (float) (starToCamera.z + rotatedRight.z * halfSize + rotatedUp.z * halfSize)
            );

            buffer.vertex(
                    (float) (starToCamera.x - rotatedRight.x * halfSize + rotatedUp.x * halfSize),
                    (float) (starToCamera.y - rotatedRight.y * halfSize + rotatedUp.y * halfSize),
                    (float) (starToCamera.z - rotatedRight.z * halfSize + rotatedUp.z * halfSize)
            );
        }
        this.starBuffer.bind();
        this.starBuffer.upload(buffer.end());
        VertexBuffer.unbind();

        RenderSystem.enableCull();
    }
    private double getModifiedStarSize(double distance, double starSize) {
        if (distance < 60) {
            // Reduce star size linearly as distance decreases from 100 to 0
            return starSize * (distance / 60);
        } else if (distance > 600) {
            // Increase star size linearly as distance increases from 200 to 1000
            return 0;
        } else if (distance > 320) {
            return starSize * (1 + (distance / 300));
        }
        return starSize;
    }

    public void render(WorldRenderContext context) {
        if (starBuffer != null) {
            RenderSystem.setShaderColor(1.0F, 0.95F, 0.9F, 1);
            BackgroundRenderer.clearFog();

            this.starBuffer.bind();




            this.starBuffer.draw(
                    context.matrixStack().peek().getPositionMatrix(),
                    context.projectionMatrix(),
                    GameRenderer.getPositionProgram()
            );
            VertexBuffer.unbind();
        }

    }


    // call before rendering, for use when moving the camera
    public void setRelativeCameraRenderPosition(DimensionalStarPosition cam) {
        getCameraRendererSingleton().setCameraPositions(cam.getX(), cam.getY(), cam.getZ());
    }


    public static DimensionalStarPosition getCameraRendererSingleton() {
        if (cameraRenderPosition == null) {
            cameraRenderPosition = new DimensionalStarPosition(0,0,0);
        }
        return cameraRenderPosition;
    }



}
