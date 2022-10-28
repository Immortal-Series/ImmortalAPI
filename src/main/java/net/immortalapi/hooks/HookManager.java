package net.immortalapi.hooks;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class HookManager {

    @Getter
    private static final HookManager instance = new HookManager();

    @Getter
    private final List<Hook> hooks = Lists.newArrayList();



}
