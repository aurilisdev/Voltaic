package voltaic.prefab.configuration;

import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import voltaic.api.configuration.*;
import voltaic.common.settings.VoltaicConfig;
import voltaic.common.settings.VoltaicConstants;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;

@Deprecated(since = "1.0.0-7", forRemoval = true)
public class ConfigurationHandler {
    // to help keep log spam down
    private static final boolean DEBUG_MODE = false;

    @Deprecated(since = "1.0.0-7", forRemoval = true)
    public static void fillFromLegacyConfig(VoltaicConfig config) {
        config.DISPENSE_GUIDEBOOK.set(VoltaicConstants.DISPENSE_GUIDEBOOK);
        config.BACKGROUND_RADIATION_DISSIPATION.set(VoltaicConstants.BACKROUND_RADIATION_DISSIPATION);
        config.IODINE_RESISTANCE_THRESHOLD.set(VoltaicConstants.IODINE_RESISTANCE_THRESHHOLD);
        config.IODINE_RAD_REDUCTION.set(VoltaicConstants.IODINE_RAD_REDUCTION);
        config.RADIATION_SYSTEM_ENABLED.set(VoltaicConstants.RADIATION_SYSTEM_ENABLED);
        config.ORES_EMIT_RADIATION.set(VoltaicConstants.ORES_EMIT_RADIATION);
        config.ORE_RADIATION_ADMIT_RATE.set(VoltaicConstants.ORE_RADIATION_ADMIT_RATE);
        config.SPEC.save();
    }

    @Deprecated(since = "1.0.0-7", forRemoval = true)
    public static void load(Class<?> clazz) {
        Configuration config = clazz.getAnnotation(Configuration.class);
        File file = new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toFile(), config.name().toLowerCase(Locale.ROOT) + ".txt");
        Field[] declaredFields = clazz.getDeclaredFields();
        try {
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (Field field : declaredFields) {
                    if (Modifier.isPublic(field.getModifiers())) {
                        String name = field.getName();
                        if (field.isAnnotationPresent(IntValue.class)) {
                            String comment = field.getAnnotation(IntValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            int value = field.getInt(null);
                            writer.write("I:default=" + field.getAnnotation(IntValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(LongValue.class)) {
                            String comment = field.getAnnotation(LongValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            long value = field.getLong(null);
                            writer.write("L:default=" + field.getAnnotation(LongValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(FloatValue.class)) {
                            String comment = field.getAnnotation(FloatValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            float value = field.getFloat(null);
                            writer.write("F:default=" + field.getAnnotation(FloatValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(DoubleValue.class)) {
                            String comment = field.getAnnotation(DoubleValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            double value = field.getDouble(null);
                            writer.write("D:default=" + field.getAnnotation(DoubleValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(StringValue.class)) {
                            String comment = field.getAnnotation(StringValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            String value = (String) field.get(null);
                            writer.write("S:default=" + field.getAnnotation(StringValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(BooleanValue.class)) {
                            String comment = field.getAnnotation(BooleanValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            boolean value = field.getBoolean(null);
                            writer.write("T:default=" + field.getAnnotation(BooleanValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(ByteValue.class)) {
                            String comment = field.getAnnotation(ByteValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            byte value = field.getByte(null);
                            writer.write("B:default=" + field.getAnnotation(ByteValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        writer.newLine();
                    }
                }
                writer.close();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                HashSet<Field> found = new HashSet<>();
                for (Field field : declaredFields) {
                    if (Modifier.isPublic(field.getModifiers())) {
                        found.add(field);
                    }
                }
                while (line != null) {
                    if (!line.startsWith("Comment: ") && !line.startsWith("//") && !line.isEmpty() && line.contains(" -> ")) {
                        line = line.substring(10);
                        line = line.substring(line.indexOf(" -> "));
                        line = line.replace("'", "");
                        line = line.substring(4);
                        String[] split = line.split("=");
                        try {
                            Field field = clazz.getDeclaredField(split[0]);
                            if (field.isAnnotationPresent(IntValue.class)) {
                                field.setInt(null, Integer.parseInt(split[1]));
                            }
                            if (field.isAnnotationPresent(LongValue.class)) {
                                field.setLong(null, Long.parseLong(split[1]));
                            }
                            if (field.isAnnotationPresent(FloatValue.class)) {
                                field.setFloat(null, Float.parseFloat(split[1]));
                            }
                            if (field.isAnnotationPresent(DoubleValue.class)) {
                                field.setDouble(null, Double.parseDouble(split[1]));
                            }
                            if (field.isAnnotationPresent(StringValue.class)) {
                                field.set(null, split[1]);
                            }
                            if (field.isAnnotationPresent(BooleanValue.class)) {
                                field.setBoolean(null, Boolean.parseBoolean(split[1]));
                            }
                            if (field.isAnnotationPresent(ByteValue.class)) {
                                field.setByte(null, Byte.parseByte(split[1]));
                            }
                            found.remove(field);
                        } catch (Exception e) {
                            System.out.println("Invalid field found in config file '" + file.getName() + "'");
                            System.out.println("Full: " + line);
                            System.out.println("Field: " + split[0]);
                            System.out.println("Value: " + split[1]);
                        }
                        if (DEBUG_MODE) {
                            System.out.println("Parsed config field '" + split[0] + "' as -> " + split[1]);
                        }
                    }
                    line = reader.readLine();
                }
                reader.close();
                file.delete();
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (Field field : declaredFields) {
                    if (Modifier.isPublic(field.getModifiers())) {
                        String name = field.getName();
                        if (field.isAnnotationPresent(IntValue.class)) {
                            String comment = field.getAnnotation(IntValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            int value = field.getInt(null);
                            writer.write("I:default=" + field.getAnnotation(IntValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(LongValue.class)) {
                            String comment = field.getAnnotation(LongValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            long value = field.getLong(null);
                            writer.write("L:default=" + field.getAnnotation(LongValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(FloatValue.class)) {
                            String comment = field.getAnnotation(FloatValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            float value = field.getFloat(null);
                            writer.write("F:default=" + field.getAnnotation(FloatValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(DoubleValue.class)) {
                            String comment = field.getAnnotation(DoubleValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            double value = field.getDouble(null);
                            writer.write("D:default=" + field.getAnnotation(DoubleValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(StringValue.class)) {
                            String comment = field.getAnnotation(StringValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            String value = (String) field.get(null);
                            writer.write("S:default=" + field.getAnnotation(StringValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(BooleanValue.class)) {
                            String comment = field.getAnnotation(BooleanValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            boolean value = field.getBoolean(null);
                            writer.write("T:default=" + field.getAnnotation(BooleanValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        if (field.isAnnotationPresent(ByteValue.class)) {
                            String comment = field.getAnnotation(ByteValue.class).comment();
                            if (!comment.isEmpty()) {
                                writer.write("Comment: '" + comment + "'");
                                writer.newLine();
                            }
                            byte value = field.getByte(null);
                            writer.write("B:default=" + field.getAnnotation(ByteValue.class).def() + " -> " + name + "='" + value + "'");
                        }
                        writer.newLine();
                    }
                }
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
