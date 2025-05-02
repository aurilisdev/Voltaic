package voltaic.prefab.utilities;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import voltaic.api.codec.StreamCodec;

/**
 * Utility class for Codecs
 *
 * @author skip999
 */
public class CodecUtils {
	
	public static final Codec<Vec3> VEC3_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			//
			Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
			//
			Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
			//
			Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z))
			//
			.apply(instance, Vec3::new));

	public static final Codec<UUID> UUID_CODEC = Codec.INT_STREAM.comapFlatMap((p_235884_) -> {
		return Util.fixedSize(p_235884_, 4).map(CodecUtils::uuidFromIntArray);
	}, (p_235888_) -> {
		return Arrays.stream(uuidToIntArray(p_235888_));
	});

	public static UUID uuidFromIntArray(int[] p_235886_) {
		return new UUID((long) p_235886_[0] << 32 | (long) p_235886_[1] & 4294967295L, (long) p_235886_[2] << 32 | (long) p_235886_[3] & 4294967295L);
	}

	public static int[] uuidToIntArray(UUID pUuid) {
		long i = pUuid.getMostSignificantBits();
		long j = pUuid.getLeastSignificantBits();
		return leastMostToIntArray(i, j);
	}

	private static int[] leastMostToIntArray(long pMost, long pLeast) {
		return new int[] { (int) (pMost >> 32), (int) pMost, (int) (pLeast >> 32), (int) pLeast };
	}

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final StreamCodec<? super B, T9> pCodec9,
            final Function<C, T9> pGetter9,
            final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);
                T9 t9 = pCodec9.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
                pCodec9.encode(buffer, pGetter9.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final StreamCodec<? super B, T9> pCodec9,
            final Function<C, T9> pGetter9,
            final StreamCodec<? super B, T10> pCodec10,
            final Function<C, T10> pGetter10,
            final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);
                T9 t9 = pCodec9.decode(buffer);
                T10 t10 = pCodec10.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
                pCodec9.encode(buffer, pGetter9.apply(object));
                pCodec10.encode(buffer, pGetter10.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final StreamCodec<? super B, T9> pCodec9,
            final Function<C, T9> pGetter9,
            final StreamCodec<? super B, T10> pCodec10,
            final Function<C, T10> pGetter10,
            final StreamCodec<? super B, T11> pCodec11,
            final Function<C, T11> pGetter11,
            final Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);
                T9 t9 = pCodec9.decode(buffer);
                T10 t10 = pCodec10.decode(buffer);
                T11 t11 = pCodec11.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
                pCodec9.encode(buffer, pGetter9.apply(object));
                pCodec10.encode(buffer, pGetter10.apply(object));
                pCodec11.encode(buffer, pGetter11.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final StreamCodec<? super B, T9> pCodec9,
            final Function<C, T9> pGetter9,
            final StreamCodec<? super B, T10> pCodec10,
            final Function<C, T10> pGetter10,
            final StreamCodec<? super B, T11> pCodec11,
            final Function<C, T11> pGetter11,
            final StreamCodec<? super B, T12> pCodec12,
            final Function<C, T12> pGetter12,
            final Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);
                T9 t9 = pCodec9.decode(buffer);
                T10 t10 = pCodec10.decode(buffer);
                T11 t11 = pCodec11.decode(buffer);
                T12 t12 = pCodec12.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
                pCodec9.encode(buffer, pGetter9.apply(object));
                pCodec10.encode(buffer, pGetter10.apply(object));
                pCodec11.encode(buffer, pGetter11.apply(object));
                pCodec12.encode(buffer, pGetter12.apply(object));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final StreamCodec<? super B, T9> pCodec9,
            final Function<C, T9> pGetter9,
            final StreamCodec<? super B, T10> pCodec10,
            final Function<C, T10> pGetter10,
            final StreamCodec<? super B, T11> pCodec11,
            final Function<C, T11> pGetter11,
            final StreamCodec<? super B, T12> pCodec12,
            final Function<C, T12> pGetter12,
            final StreamCodec<? super B, T13> pCodec13,
            final Function<C, T13> pGetter13,
            final Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = pCodec1.decode(buffer);
                T2 t2 = pCodec2.decode(buffer);
                T3 t3 = pCodec3.decode(buffer);
                T4 t4 = pCodec4.decode(buffer);
                T5 t5 = pCodec5.decode(buffer);
                T6 t6 = pCodec6.decode(buffer);
                T7 t7 = pCodec7.decode(buffer);
                T8 t8 = pCodec8.decode(buffer);
                T9 t9 = pCodec9.decode(buffer);
                T10 t10 = pCodec10.decode(buffer);
                T11 t11 = pCodec11.decode(buffer);
                T12 t12 = pCodec12.decode(buffer);
                T13 t13 = pCodec13.decode(buffer);

                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
            }

            @Override
            public void encode(B buffer, C object) {
                pCodec1.encode(buffer, pGetter1.apply(object));
                pCodec2.encode(buffer, pGetter2.apply(object));
                pCodec3.encode(buffer, pGetter3.apply(object));
                pCodec4.encode(buffer, pGetter4.apply(object));
                pCodec5.encode(buffer, pGetter5.apply(object));
                pCodec6.encode(buffer, pGetter6.apply(object));
                pCodec7.encode(buffer, pGetter7.apply(object));
                pCodec8.encode(buffer, pGetter8.apply(object));
                pCodec9.encode(buffer, pGetter9.apply(object));
                pCodec10.encode(buffer, pGetter10.apply(object));
                pCodec11.encode(buffer, pGetter11.apply(object));
                pCodec12.encode(buffer, pGetter12.apply(object));
                pCodec13.encode(buffer, pGetter13.apply(object));
            }
        };
    }


}
