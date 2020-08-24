package org.wonderming.manager.access;

import org.wonderming.manager.CoreAccess;

import java.lang.reflect.Modifier;

/**
 * @author wangdeming
 **/
public abstract class BaseModifierAccess implements CoreAccess {

    private final int modifiers;

    public BaseModifierAccess(int modifiers){
        this.modifiers = modifiers;
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(modifiers);
    }

    @Override
    public boolean isNative() {
        return Modifier.isNative(modifiers);
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }
}
