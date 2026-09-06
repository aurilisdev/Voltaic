package voltaic.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

import org.jetbrains.annotations.NotNull;

@NotNull
@Nonnull // This must be here for correct analysis even though it may give a warning.
@Documented
@TypeQualifierDefault({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface NothingNullByDefault {
}