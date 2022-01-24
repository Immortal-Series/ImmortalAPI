package me.lukeben.utils;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class MenuFiller {

    private final List<Integer> slots;
    private final ItemBuilder fillerItem;

    public MenuFiller(List<Object> slots, ItemBuilder filler) {
        this.slots = getSlotArray(slots);
        this.filler_item = filler;
    }

    public List<Integer> getSlotArray(List<Object> list) {
        List<Integer> slotArray = Lists.newArrayList();
        for(Object object : list) {
            if(object instanceof String) {
                String text = String.valueOf(object);
                if(!text.contains("-")) continue;
                String[] textArray = text.split("-");
                if(!Methods.isInt(textArray[0]) || !Methods.isInt(textArray[1])) continue;
                int one = Integer.parseInt(textArray[0]);
                int two = Integer.parseInt(textArray[1]);
                for(int i = one; i <= two; i++) {
                    slotArray.add(i);
                }
            } else if(object instanceof Integer) {
                slotArray.add(Integer.parseInt(String.valueOf(object)));
            }
        }
        return slotArray;
    }


}
