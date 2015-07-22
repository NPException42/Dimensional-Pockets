/**
 * (C) 2015 NPException
 */
package net.gtn.dimensionalpocket.common.core.utils;

import java.util.Properties;

import net.gtn.dimensionalpocket.common.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import de.npe.gameanalytics.SimpleAnalytics;
import de.npe.gameanalytics.events.GADesignEvent;
import de.npe.gameanalytics.events.GAErrorEvent.Severity;
import de.npe.gameanalytics.events.GAEvent;


/**
 * @author NPException
 *
 */
public class DPAnalytics extends SimpleAnalytics {
	public static final String GA_GAME_KEY = "da1d2ad5b654b795d187f0a2cc8d0609";
	public static final String GA_SECRET_KEY = "760658a7191e5d29f3a6cc9f8ad325ed88631535";

	public static final DPAnalytics analytics = new DPAnalytics();

	private static final String MAIN_POCKET = "Pocket:";

	private static final String SUB_PLAYER = "Player:";
	private static final String SUB_TELEPORT = "Teleport:";
	private static final String SUB_TRANSFER = "Transfer:";
	private static final String SUB_STATE = "State:";
	private static final String SUB_CRAFTED = "Crafted:";
	private static final String SUB_ENERGY_RF = "EnergyRF:";
	private static final String SUB_FLUIDS = "Fluids:";
	private static final String SUB_TRAPPED = "Trapped:";

	private static final String TRAPPED_INSIDE_NOT_PLACED = "Inside_NotPlaced";
	private static final String TRAPPED_INSIDE_EXIT_BLOCKED = "Inside_ExitBlocked";
	private static final String TRAPPED_OUTSIDE_EXIT_BLOCKED = "Outside_ExitBlocked";
	private static final String DIRECTION_IN = "In";
	private static final String DIRECTION_OUT = "Out";


	private static final String ANALYITCS_PLAYER_TELEPORT_IN = MAIN_POCKET + SUB_PLAYER + SUB_TELEPORT + DIRECTION_IN;
	private static final String ANALYITCS_PLAYER_TELEPORT_OUT = MAIN_POCKET + SUB_PLAYER + SUB_TELEPORT + DIRECTION_OUT;
	private static final String ANALYITCS_PLAYER_TRAPPED_INSIDE_NOT_PLACED = MAIN_POCKET + SUB_PLAYER + SUB_TRAPPED + TRAPPED_INSIDE_NOT_PLACED;
	private static final String ANALYITCS_PLAYER_TRAPPED_INSIDE_EXIT_BLOCKED = MAIN_POCKET + SUB_PLAYER + SUB_TRAPPED + TRAPPED_INSIDE_EXIT_BLOCKED;
	private static final String ANALYITCS_PLAYER_TRAPPED_OUTSIDE_EXIT_BLOCKED = MAIN_POCKET + SUB_PLAYER + SUB_TRAPPED + TRAPPED_OUTSIDE_EXIT_BLOCKED;
	private static final String ANALYITCS_TRANSFER_ENERGY_RF_IN = MAIN_POCKET + SUB_TRANSFER + SUB_ENERGY_RF + DIRECTION_IN;
	private static final String ANALYITCS_TRANSFER_ENERGY_RF_OUT = MAIN_POCKET + SUB_TRANSFER + SUB_ENERGY_RF + DIRECTION_OUT;
	private static final String ANALYITCS_TRANSFER_FLUIDS_IN = MAIN_POCKET + SUB_TRANSFER + SUB_FLUIDS + DIRECTION_IN;
	private static final String ANALYITCS_TRANSFER_FLUIDS_OUT = MAIN_POCKET + SUB_TRANSFER + SUB_FLUIDS + DIRECTION_OUT;
	private static final String ANALYTICS_POCKET_PLACED = MAIN_POCKET + SUB_STATE + "Placed";
	private static final String ANALYTICS_POCKET_MINED = MAIN_POCKET + SUB_STATE + "Mined";
	private static final String ANALYTICS_POCKET_CRAFTED_PLAYER = MAIN_POCKET + SUB_STATE + SUB_CRAFTED + "Player";
	private static final String ANALYTICS_POCKET_CRAFTED_AUTOMATION = MAIN_POCKET + SUB_STATE + SUB_CRAFTED + "Automation";


	public DPAnalytics() {
		super(Reference.VERSION, GA_GAME_KEY, GA_SECRET_KEY);
	}

	@Override
	public boolean isActive() {
		return Reference.MAY_COLLECT_ANONYMOUS_USAGE_DATA &&
				(isClient() ? Minecraft.getMinecraft().isSnooperEnabled() : isServerSnooper());
	}

	/**
	 * We try to grab the snooper settings from the server. If they are not yet
	 * initialized, we assume true.
	 *
	 * @return
	 */
	private static boolean isServerSnooper() {
		try {
			return MinecraftServer.getServer().isSnooperEnabled();
		} catch (NullPointerException npe) {
			return true;
		}
	}

	@Override
	protected String getConfigFileName() {
		return "net.gtn.dimensionalpocket.DPAnalytics";
	}

	////////////////////////////////////////////
	// Logging of player teleport (-attempts) //
	////////////////////////////////////////////

	private GAEvent teleportIn;

	public void logPlayerTeleportInEvent() {
		if (teleportIn == null) {
			teleportIn = new GADesignEvent(this, ANALYITCS_PLAYER_TELEPORT_IN, null, Float.valueOf(1f));
		}
		event(teleportIn, false);
	}

	private GAEvent teleportOut;

	public void logPlayerTeleportOutEvent() {
		if (teleportOut == null) {
			teleportOut = new GADesignEvent(this, ANALYITCS_PLAYER_TELEPORT_OUT, null, Float.valueOf(1f));
		}
		event(teleportOut, false);
	}

	private GAEvent trappedNotPlaced;

	public void logPlayerTrappedInside_NotPlaced_Event() {
		if (trappedNotPlaced == null) {
			trappedNotPlaced = new GADesignEvent(this, ANALYITCS_PLAYER_TRAPPED_INSIDE_NOT_PLACED, null, Float.valueOf(1f));
		}
		event(trappedNotPlaced, false);
	}

	private GAEvent trappedBlocked;

	public void logPlayerTrappedInside_ExitBlocked_Event() {
		if (trappedBlocked == null) {
			trappedBlocked = new GADesignEvent(this, ANALYITCS_PLAYER_TRAPPED_INSIDE_EXIT_BLOCKED, null, Float.valueOf(1f));
		}
		event(trappedBlocked, false);
	}

	private GAEvent trappedOutside;

	public void logPlayerTrappedOutside_ExitBlocked_Event() {
		if (trappedOutside == null) {
			trappedOutside = new GADesignEvent(this, ANALYITCS_PLAYER_TRAPPED_OUTSIDE_EXIT_BLOCKED, null, Float.valueOf(1f));
		}
		event(trappedOutside, false);
	}

	///////////////////////////////////
	// Logging of RF energy transfer //
	///////////////////////////////////

	public void logRFTransferIn(int amount) {
		eventDesign(ANALYITCS_TRANSFER_ENERGY_RF_IN, Integer.valueOf(amount));
	}

	public void logRFTransferOut(int amount) {
		eventDesign(ANALYITCS_TRANSFER_ENERGY_RF_OUT, Integer.valueOf(amount));
	}

	//////////////////////////////////
	// Logging of fluid mb transfer //
	//////////////////////////////////

	public void logFluidTransferIn(int amount) {
		eventDesign(ANALYITCS_TRANSFER_FLUIDS_IN, Integer.valueOf(amount));
	}

	public void logFluidTransferOut(int amount) {
		eventDesign(ANALYITCS_TRANSFER_FLUIDS_OUT, Integer.valueOf(amount));
	}

	////////////////////////////////////////////////
	// Logging of pocket placed + mined + crafted //
	////////////////////////////////////////////////

	private GAEvent pocketPlaced;

	public void logPocketPlaced() {
		if (pocketPlaced == null) {
			pocketPlaced = new GADesignEvent(this, ANALYTICS_POCKET_PLACED, null, Float.valueOf(1f));
		}
		event(pocketPlaced, false);
	}

	private GAEvent pocketMined;

	public void logPocketMined() {
		if (pocketMined == null) {
			pocketMined = new GADesignEvent(this, ANALYTICS_POCKET_MINED, null, Float.valueOf(1f));
		}
		event(pocketMined, false);
	}

	public void logPocketsCraftedByPlayer(int amount) {
		eventDesign(ANALYTICS_POCKET_CRAFTED_PLAYER, Integer.valueOf(amount));
	}

	public void logPocketsCraftedByAutomation(int amount) {
		eventDesign(ANALYTICS_POCKET_CRAFTED_AUTOMATION, Integer.valueOf(amount));
	}

	///////////////////
	// Shutdown hook //
	///////////////////

	private static boolean hasRegisteredCrash;

	/**
	 * Creates a shutdown hook that looks for crashes
	 */
	public void initShutdownHook() {
		FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
			@Override
			public String call() throws Exception {
				hasRegisteredCrash = true;
				return analytics.isActive() ? "Will analyze crash-log before shutdown and send error to developer if DimensionalPockets might be involved." : "[inactive]";
			}

			@Override
			public String getLabel() {
				return "DPAnalytics Crash Check";
			}
		});

		// startup check. 1) to initialize the crash analyzer. 2) to get a crashlog we might not had the time to check yet
		checkCrashLogs();

		Runtime.getRuntime().addShutdownHook(new Thread("DPAnalytics-ShutdownHook") {
			@Override
			public void run() {
				if (analytics.isActive()) {
					if (hasRegisteredCrash) {
						checkCrashLogs();
					} else {
						System.out.println("No crash, we are good.");
					}
				}
			}
		});
	}

	private void checkCrashLogs() {
		try {
			Properties config = analytics.loadConfig();
			String message = DPCrashAnalyzer.analyzeCrash(config, analytics.isClient());
			if (message != null) {
				analytics.saveConfig(config);
				DPAnalytics.this.eventErrorNOW(Severity.critical, message);
			}
		} catch (Exception ex) {
			DPLogger.warning("We tried to analyze crash reports but failed for some reason: " + ex);
		}
	}
}
