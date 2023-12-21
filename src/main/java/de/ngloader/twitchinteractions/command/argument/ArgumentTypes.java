package de.ngloader.twitchinteractions.command.argument;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.util.ReflectionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.OperationArgument.Operation;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ArgumentTypes {

	private static final Class<?> CLASS_CRAFT_SERVER = ReflectionUtil.getCraftBukkitClass("CraftServer");
	private static final Object INSTANCE_CRAFT_SERVER = CLASS_CRAFT_SERVER.cast(Bukkit.getServer());

	private static final Class<?> CLASS_CRAFT_ENTITY = ReflectionUtil.getCraftBukkitClass("entity.CraftEntity");
	private static final Method METHOD_CRAFT_ENTITY_GET_ENTITY = ReflectionUtil.getMethod(CLASS_CRAFT_ENTITY, "getEntity", CLASS_CRAFT_SERVER, Entity.class);

	private static final Class<?> CLASS_VANILLA_COMMAND_WRAPPER = ReflectionUtil.getCraftBukkitClass("command.VanillaCommandWrapper");
	private static final Method METHOD_VANILLA_COMMAND_WRAPPER_GET_LISTENER = ReflectionUtil.getMethod(CLASS_VANILLA_COMMAND_WRAPPER, "getListener", CommandSender.class);

	private static final Class<?> CLASS_CRAFT_MAGIC_NUMBERS = ReflectionUtil.getCraftBukkitClass("util.CraftMagicNumbers");
	private static final Method METHOD_CRAFT_MAGIC_NUMBERS_GET_MATERIAL_ITEM = ReflectionUtil.getMethod(CLASS_CRAFT_MAGIC_NUMBERS, "getMaterial", Item.class);
	private static final Method METHOD_CRAFT_MAGIC_NUMBERS_GET_MATERIAL_BLOCK_STATE = ReflectionUtil.getMethod(CLASS_CRAFT_MAGIC_NUMBERS, "getMaterial", BlockState.class);

	private static final Class<?> CLASS_CRAFT_ITEM_STACK = ReflectionUtil.getCraftBukkitClass("inventory.CraftItemStack");
	private static final Method METHOD_CRAFT_ITEM_STACK_AS_BUKKIT_COPY = ReflectionUtil.getMethod(CLASS_CRAFT_ITEM_STACK, "asBukkitCopy", ItemStack.class);

	public static CommandBuildContext holderLookup(Registry<?> registry) {
		return CommandBuildContext.simple(HolderLookup.Provider.create(Stream.of(registry.asLookup())), FeatureFlagSet.of());
	}

	private static org.bukkit.entity.Entity convertToEntity(Entity entity) {
		try {
			return (org.bukkit.entity.Entity) METHOD_CRAFT_ENTITY_GET_ENTITY.invoke(null, INSTANCE_CRAFT_SERVER, entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Player convertToPlayer(ServerPlayer player) {
		return (Player) convertToEntity(player);
	}

	private static World convertToWorld(ServerLevel level) {
		return Bukkit.getWorld(level.uuid);
	}

	private static Material convertToMaterial(Item item) {
		try {
			return (Material) METHOD_CRAFT_MAGIC_NUMBERS_GET_MATERIAL_ITEM.invoke(null, item);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Material convertToMaterial(BlockState blockState) {
		try {
			return (Material) METHOD_CRAFT_MAGIC_NUMBERS_GET_MATERIAL_BLOCK_STATE.invoke(null, blockState);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static org.bukkit.inventory.ItemStack convertToItemStack(ItemStack item) {
		try {
			return (org.bukkit.inventory.ItemStack) METHOD_CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, item);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static CommandContext<CommandSourceStack> convertToSourceStack(CommandContext<CommandSender> context) {
		return new CommandContextWrapper(context);
	}

	public static CommandSourceStack getSourceStack(CommandSender sender) {
		try {
			return (CommandSourceStack) METHOD_VANILLA_COMMAND_WRAPPER_GET_LISTENER.invoke(null, sender);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new NullPointerException(e.getMessage());
		}
	}

	/**
	 * Examples: "stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}"
	 */
	public static BlockStateArgument block() {
		return BlockStateArgument.block(holderLookup(BuiltInRegistries.BLOCK)); //TODO testing
	}

	public static BlockInput getNMSBlock(CommandContext<CommandSender> context, String name) {
		return BlockStateArgument.getBlock(convertToSourceStack(context), name);
	}

	public static Material getBlock(CommandContext<CommandSender> context, String name) {
		return ArgumentTypes.convertToMaterial(BlockStateArgument.getBlock(convertToSourceStack(context), name).getState());
	}

	/**
	 * Examples: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5"
	 */
	public BlockPosArgument blockPos() {
		return BlockPosArgument.blockPos();
	}

	public static BlockPos getNMSBlockPosLoaded(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return BlockPosArgument.getLoadedBlockPos(convertToSourceStack(context), name);
	}

	public static BlockPos getNMSBlockPosSpawnable(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return BlockPosArgument.getSpawnablePos(convertToSourceStack(context), name);
	}

	public static Vec3 getNMSBlockPos(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, Coordinates.class).getPosition(getSourceStack(context.getSource()));
	}

	public static Vec2 getNMSBlockPosRotation(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, Coordinates.class).getRotation(getSourceStack(context.getSource()));
	}

	public static Location getBlockPos(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		Vec3 location = ArgumentTypes.getNMSBlockPos(context, name);
		return new Location(context.getSource() instanceof Player player ? player.getWorld() : null, location.x(), location.y(), location.z());
	}

	/**
	 * Examples: "0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0"
	 */
	public static ColumnPosArgument columnPos() {
		return ColumnPosArgument.columnPos();
	}

	public static ColumnPos getNMSColumnPos(CommandContext<CommandSender> context, String name) {
		return ColumnPosArgument.getColumnPos(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "0 0", "~ ~", "~-5 ~5"
	 */
	public static RotationArgument rotation() {
		return RotationArgument.rotation();
	}

	public static Coordinates getRotation(CommandContext<CommandSender> context, String name) {
		return RotationArgument.getRotation(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "xyz", "x
	 */
	public static SwizzleArgument swizzle() {
		return SwizzleArgument.swizzle();
	}

	public static EnumSet<Axis> getSwizzle(CommandContext<CommandSender> context, String name) {
		return SwizzleArgument.getSwizzle(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "0 0", "~ ~", "0.1 -0.5", "~1 ~-2"
	 */
	public static Vec2Argument vec2() {
		return Vec2Argument.vec2();
	}

	public static Vec2Argument vec2(boolean centerCorrect) {
		return Vec2Argument.vec2(centerCorrect);
	}

	public static Vec2 getVec2(CommandContext<CommandSender> context, String name) {
		return Vec2Argument.getVec2(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5"
	 */
	public static Vec3Argument vec3() {
		return Vec3Argument.vec3();
	}

	public static Vec3Argument vec3(boolean centerCorrect) {
		return Vec3Argument.vec3(centerCorrect);
	}

	public static Vec3 getVec3(CommandContext<CommandSender> context, String name) {
		return Vec3Argument.getVec3(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "stick", "minecraft:stick", "stick{foo=bar}"
	 */
	public static ItemArgument item() {
		return ItemArgument.item(holderLookup(BuiltInRegistries.ITEM));
	}

	public static ItemInput getNMSItem(CommandContext<CommandSender> context, String name) {
		return ItemArgument.getItem(convertToSourceStack(context), name);
	}

	public static Material getItem(CommandContext<CommandSender> context, String name) {
		return ArgumentTypes.convertToMaterial(ItemArgument.getItem(convertToSourceStack(context), name).getItem());
	}

	public static org.bukkit.inventory.ItemStack getItemStack(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ArgumentTypes.getItemStack(context, name, 1);
	}

	public static org.bukkit.inventory.ItemStack getItemStack(CommandContext<CommandSender> context, String name, int amount) throws CommandSyntaxException {
		return ArgumentTypes.convertToItemStack(ItemArgument.getItem(convertToSourceStack(context), name).createItemStack(amount, false));
	}

	/**
	 * Examples: "0", "~", "~-5"
	 */
	public static ArgumentType<?> angle() {
		return AngleArgument.angle();
	}

	public static float getAngle(CommandContext<CommandSender> context, String name) {
		return AngleArgument.getAngle(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "red", "green"
	 */
	public static ArgumentType<?> color() {
		return ColorArgument.color();
	}

	public static ChatFormatting getNMSColor(CommandContext<CommandSender> context, String name) {
		return ColorArgument.getColor(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"
	 */
	public static ArgumentType<?> textComponent() {
		return ComponentArgument.textComponent();
	}

	public static Component getNMSComponent(CommandContext<CommandSender> context, String name) {
		return ComponentArgument.getComponent(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "{}", "{foo=bar}"
	 */
	public static ArgumentType<?> compoundTag() {
		return CompoundTagArgument.compoundTag();
	}

	public static CompoundTag getNMSCompoundTag(CommandContext<CommandSender> context, String name) {
		return CompoundTagArgument.getCompoundTag(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "overworld", "nether"
	 */
	public static ArgumentType<?> dimension() {
		return DimensionArgument.dimension();
	}

	public static ServerLevel getNMSDimension(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return DimensionArgument.getDimension(convertToSourceStack(context), name);
	}

	public static World getDimension(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ArgumentTypes.convertToWorld(DimensionArgument.getDimension(convertToSourceStack(context), name));
	}

	/**
	 * Examples: "eyes", "feet"
	 */
	public static ArgumentType<?> entityAnchor() {
		return EntityAnchorArgument.anchor();
	}

	public static Anchor getNMSAnchor(CommandContext<CommandSender> context, String name) {
		return EntityAnchorArgument.getAnchor(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
	 */
	public static ArgumentType<?> entity() {
		return EntityArgumentWrapper.entity();
	}

	public static Entity getNMSEntity(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getEntity(convertToSourceStack(context), name);
	}

	public static org.bukkit.entity.Entity getEntity(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ArgumentTypes.convertToEntity(EntityArgument.getEntity(convertToSourceStack(context), name));
	}

	/**
	 * Examples: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
	 */
	public static ArgumentType<?> entities() {
		return EntityArgumentWrapper.entities();
	}

	public static Collection<? extends Entity> getNMSEntities(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getEntities(convertToSourceStack(context), name);
	}

	public static List<org.bukkit.entity.Entity> getEntities(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getEntities(convertToSourceStack(context), name).stream().map(ArgumentTypes::convertToEntity).toList();
	}

	public static Collection<? extends Entity> getNMSOptionalEntities(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getOptionalEntities(convertToSourceStack(context), name);
	}

	public static List<org.bukkit.entity.Entity> getOptionalEntities(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getOptionalEntities(convertToSourceStack(context), name).stream().map(ArgumentTypes::convertToEntity).toList();
	}

	public static Collection<ServerPlayer> getNMSOptionalPlayers(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getOptionalPlayers(convertToSourceStack(context), name);
	}

	public static List<Player> getOptionalPlayers(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getOptionalPlayers(convertToSourceStack(context), name).stream().map(ArgumentTypes::convertToPlayer).toList();
	}

	/**
	 * Examples: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
	 */
	public static ArgumentType<?> player() {
		return EntityArgumentWrapper.player();
	}

	public static ServerPlayer getNMSPlayer(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getPlayer(convertToSourceStack(context), name);
	}

	public static Player getPlayer(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ArgumentTypes.convertToPlayer(EntityArgument.getPlayer(convertToSourceStack(context), name));
	}

	/**
	 * Examples: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
	 */
	public static ArgumentType<?> players() {
		return EntityArgumentWrapper.players();
	}

	public static Collection<ServerPlayer> getNMSPlayers(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getPlayers(convertToSourceStack(context), name);
	}

	public static List<Player> getPlayers(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return EntityArgument.getPlayers(convertToSourceStack(context), name).stream().map(ArgumentTypes::convertToPlayer).toList();
	}

	/**
	 * Examples: "Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"
	 */
	public static ArgumentType<?> gameProfile() {
		return GameProfileArgument.gameProfile();
	}

	public static Collection<GameProfile> getNMSGameProfiles(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return GameProfileArgument.getGameProfiles(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "Hello world!", "foo", "@e", "Hello @p :)"
	 */
	public static ArgumentType<?> message() {
		return MessageArgument.message();
	}

	public static Component getNMSMessage(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return MessageArgument.getMessage(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}"
	 */
	public static ArgumentType<?> nbtPath() {
		return NbtPathArgument.nbtPath();
	}

	public static NbtPath getNMSPath(CommandContext<CommandSender> context, String name) {
		return NbtPathArgument.getPath(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]"
	 */
	public static ArgumentType<?> nbtTag() {
		return NbtTagArgument.nbtTag();
	}

	public static Tag getNMSNbtTag(CommandContext<CommandSender> context, String name) {
		return NbtTagArgument.getNbtTag(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "foo", "*", "012"
	 */
	public static ArgumentType<?> objective() {
		return ObjectiveArgument.objective();
	}

	public static Objective getNMSObjective(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ObjectiveArgument.getObjective(convertToSourceStack(context), name);
	}

	public static Objective getNMSWriteableObjective(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ObjectiveArgument.getWritableObjective(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "foo", "foo.bar.baz", "minecraft:foo"
	 */
	public static ArgumentType<?> criteria() {
		return ObjectiveCriteriaArgument.criteria();
	}

	public static ObjectiveCriteria getNMSCriteria(CommandContext<CommandSender> context, String name) {
		return ObjectiveCriteriaArgument.getCriteria(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "=", ">", "<"
	 */
	public static ArgumentType<?> operation() {
		return OperationArgument.operation();
	}

	public static Operation getNMSOperation(CommandContext<CommandSender> context, String name) {
		return OperationArgument.getOperation(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "foo", "foo:bar", "012"
	 */
	public static ArgumentType<?> resourceLocationId() {
		return ResourceLocationArgument.id();
	}

	public static AdvancementHolder getNMSAdvancement(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ResourceLocationArgument.getAdvancement(convertToSourceStack(context), name);
	}

	public static RecipeHolder<?> getNMSRecipe(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ResourceLocationArgument.getRecipe(convertToSourceStack(context), name);
	}

	public static LootItemFunction getNMSItemModifier(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ResourceLocationArgument.getItemModifier(convertToSourceStack(context), name);
	}

	public static ResourceLocation getNMSResourceLocationId(CommandContext<CommandSender> context, String name) {
		return ResourceLocationArgument.getId(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "sidebar", "foo.bar"
	 */
	public static ArgumentType<?> displaySlot() {
		return ScoreboardSlotArgument.displaySlot();
	}

	public static DisplaySlot getDisplaySlot(CommandContext<CommandSender> context, String name) {
		return ScoreboardSlotArgument.getDisplaySlot(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "Player", "0123", "*", "@e"
	 */
	public static ArgumentType<?> scoreHolder() {
		return ScoreHolderArgument.scoreHolder();
	}

	/**
	 * Examples: "Player", "0123", "*", "@e"
	 */
	public static ArgumentType<?> scoreHolders() {
		return ScoreHolderArgument.scoreHolders();
	}

	public static String getName(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ScoreHolderArgument.getName(convertToSourceStack(context), name);
	}

	public static Collection<String> getNames(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ScoreHolderArgument.getNames(convertToSourceStack(context), name);
	}

	public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return ScoreHolderArgument.getNamesWithDefaultWildcard(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "container.5", "12", "weapon"
	 */
	public static ArgumentType<?> slot() {
		return SlotArgument.slot();
	}

	public static int getSlot(CommandContext<CommandSender> context, String name) {
		return SlotArgument.getSlot(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "foo", "123"
	 */
	public static ArgumentType<?> team() {
		return TeamArgument.team();
	}

	public static PlayerTeam getNMSTeam(CommandContext<CommandSender> context, String name) throws CommandSyntaxException {
		return TeamArgument.getTeam(convertToSourceStack(context), name);
	}

	/**
	 * Converted into tick's
	 * 
	 * Examples: "0d", "0s", "0t", "0"
	 */
	public static ArgumentType<?> time() {
		return TimeArgumentType.time();
	}

	/**
	 * Converted into tick's
	 */
	public static Long getTime(CommandContext<CommandSender> context, String name) {
		return TimeArgumentType.getTime(context, name);
	}

	/**
	 * Examples: "dd12be42-52a9-4a91-a8a1-11c01849e498"
	 * Regex: "^([-A-Fa-f0-9]+)"
	 */
	public static ArgumentType<?> uuid() {
		return UuidArgument.uuid();
	}

	public static UUID getUUID(CommandContext<CommandSender> context, String name) {
		return UuidArgument.getUuid(convertToSourceStack(context), name);
	}

	/**
	 * Examples: "true", "false"
	 */
	public static BoolArgumentType bool() {
		return BoolArgumentType.bool();
	}

	public static boolean getBoolean(CommandContext<?> context, String name) {
		return BoolArgumentType.getBool(context, name);
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static DoubleArgumentType doubleArg() {
		return DoubleArgumentType.doubleArg();
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static DoubleArgumentType doubleArg(double min) {
		return DoubleArgumentType.doubleArg(min);
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static DoubleArgumentType doubleArg(double min, double max) {
		return DoubleArgumentType.doubleArg(min, max);
	}

	public static double getDouble(CommandContext<?> context, String name) {
		return DoubleArgumentType.getDouble(context, name);
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static FloatArgumentType floatArg() {
		return FloatArgumentType.floatArg();
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static FloatArgumentType floatArg(float min) {
		return FloatArgumentType.floatArg(min);
	}

	/**
	 * Examples: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
	 */
	public static FloatArgumentType floatArg(float min, float max) {
		return FloatArgumentType.floatArg(min, max);
	}

	public static float getFloat(CommandContext<?> context, String name) {
		return FloatArgumentType.getFloat(context, name);
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static IntegerArgumentType integer() {
		return IntegerArgumentType.integer();
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static IntegerArgumentType integer(int min) {
		return IntegerArgumentType.integer(min);
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static IntegerArgumentType integer(int min, int max) {
		return IntegerArgumentType.integer(min, max);
	}

	public static int getInteger(CommandContext<?> context, String name) {
		return IntegerArgumentType.getInteger(context, name);
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static LongArgumentType longArg() {
		return LongArgumentType.longArg();
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static LongArgumentType longArg(long min) {
		return LongArgumentType.longArg(min);
	}

	/**
	 * Examples: "0", "123", "-123"
	 */
	public static LongArgumentType longArg(long min, long max) {
		return LongArgumentType.longArg(min, max);
	}

	public static long getLong(CommandContext<?> context, String name) {
		return LongArgumentType.getLong(context, name);
	}

	/**
	 * Examples: "word", "words with spaces", "\"and symbols\""
	 */
	public static StringArgumentType greedyString() {
		return StringArgumentType.greedyString();
	}

	/**
	 * Examples: "\"quoted phrase\"", "word", "\"\""
	 */
	public static StringArgumentType string() {
		return StringArgumentType.string();
	}

	/**
	 * Examples: "word", "words_with_underscores"
	 */
	public static StringArgumentType word() {
		return StringArgumentType.word();
	}

	public static String getString(CommandContext<?> context, String name) {
		return StringArgumentType.getString(context, name);
	}

	/**
	 * public enum Test {
	 * 	FOO,
	 *  BAR;
	 * }
	 * 
	 * Examples: "FOO", "BAR"
	 */
	public static <T extends Enum<?>> EnumArgumentType<T> enumType(Class<T> enumClass) {
		return EnumArgumentType.enumType(enumClass);
	}

	public static <T extends Enum<?>> T getEnum(CommandContext<?> context, String name) {
		return EnumArgumentType.getEnum(context, name);
	}
}