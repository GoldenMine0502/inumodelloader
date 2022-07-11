package kr.goldenmine.inumodelloader.inumodelloader;

import kr.goldenmine.inumodelloader.inumodelloader.block.ModBlocks;
import kr.goldenmine.inumodelloader.inumodelloader.block.ModWoodTypes;
import kr.goldenmine.inumodelloader.inumodelloader.item.ModItems;
import kr.goldenmine.inumodelloader.inumodelloader.tileentity.ModTileEntities;
import kr.goldenmine.inumodelloader.inumodelloader.tileentity.InuSignTileEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WoodType;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Inumodelloader.MOD_ID)
public class Inumodelloader {

    public static final String MOD_ID = "inumodelloader";
    public static final String MOD_NAME = "INUModelLoader";
    public static final String VERSION = "1.0-SNAPSHOT";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Inumodelloader() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModTileEntities.register(eventBus);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        event.enqueueWork(() -> {
            WoodType.register(ModWoodTypes.INUWood);
        });
//        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TREE_BLOCK.get(), RenderType.glintTranslucent());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("inumodelloader", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        event.enqueueWork(() -> {
            RenderType cutoutMipped = RenderType.getCutoutMipped();

            RenderTypeLookup.setRenderLayer(ModBlocks.TALL_INU_DOOR_BLOCK.get(), cutoutMipped);
            RenderTypeLookup.setRenderLayer(ModBlocks.TEST_OBJ_BLOCK.get(), cutoutMipped);

            ClientRegistry.bindTileEntityRenderer(ModTileEntities.SIGN_TILE_ENTITIES.get(), InuSignTileEntityRenderer::new);
            ClientRegistry.bindTileEntityRenderer(ModTileEntities.SIGN_TILE_ENTITIES_101.get(), InuSignTileEntityRenderer::new);
//            ClientRegistry.bindTileEntityRenderer(ModTileEntities.SIGN_TILE_ENTITIES_101.get(), InuSignTileEntityRenderer::new);
            Atlases.addWoodType(ModWoodTypes.INUWood);
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
