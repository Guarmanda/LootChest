package eu.decentholo.holograms.api.holograms.objects;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.decentholo.holograms.api.holograms.enums.EnumFlag;

@Getter
public abstract class FlagHolder {

    protected final Set<EnumFlag> flags = Collections.synchronizedSet(new HashSet<>());

    public boolean hasFlag(@NonNull EnumFlag flag) {
        return flags.contains(flag);
    }

}
