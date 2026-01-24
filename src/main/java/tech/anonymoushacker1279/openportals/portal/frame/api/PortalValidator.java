package tech.anonymoushacker1279.openportals.portal.frame.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for validating portal frame structures. Responsible for checking if a portal frame is valid and properly
 * formed.
 */
public interface PortalValidator {

	/**
	 * Initialize the validator with the given parameters and scan for a valid portal frame.
	 *
	 * @param level       the level to scan in
	 * @param blockPos    the starting position to check
	 * @param axis        the axis along which the portal is oriented
	 * @param foundations the blocks that can serve as portal frame blocks
	 * @return this validator instance for method chaining
	 */
	PortalValidator init(LevelAccessor level, BlockPos blockPos, Direction.Axis axis, Block... foundations);

	/**
	 * Check if the portal frame is already lit (contains portal blocks).
	 *
	 * @return true if the portal is already lit
	 */
	boolean isAlreadyLitPortalFrame();

	/**
	 * Check if the current portal frame structure is valid.
	 *
	 * @return true if the frame is valid
	 */
	boolean isValidFrame();

	/**
	 * Check if the portal frame matches the requested size constraints.
	 *
	 * @param attemptWidth  the required width (0 for no constraint)
	 * @param attemptHeight the required height (0 for no constraint)
	 * @return true if the size matches the constraints
	 */
	boolean isRequestedSize(int attemptWidth, int attemptHeight);

	/**
	 * Get the rectangular bounds of the validated portal frame.
	 *
	 * @return the portal rectangle, or null if no valid frame was found
	 */
	@Nullable
	BlockUtil.FoundRectangle getRectangle();

	/**
	 * Check if a portal of the appropriate size can fit at the given position.
	 *
	 * @param level      the level to check in
	 * @param attemptPos the position to attempt portal placement
	 * @param axis       the axis along which the portal should be oriented
	 * @return the position where the portal can fit, or null if it cannot
	 */
	@Nullable
	BlockPos doesPortalFitAt(LevelAccessor level, BlockPos attemptPos, Direction.Axis axis);
}