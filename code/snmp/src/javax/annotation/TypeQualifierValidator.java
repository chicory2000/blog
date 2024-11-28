package javax.annotation;

import java.lang.annotation.Annotation;


public interface TypeQualifierValidator<A extends Annotation> {
    @Nonnull
    When forConstantValue(@Nonnull A a, Object obj);
}
