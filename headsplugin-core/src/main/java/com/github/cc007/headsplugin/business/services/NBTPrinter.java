package com.github.cc007.headsplugin.business.services;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.utils.nmsmappings.ReflectionMethod;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Component
@Log4j2
public class NBTPrinter {

    public void printNBTItem(NBTItem nbtItem) {
        log.info("NBT values:");
        printNBTCompound(nbtItem, 0);
    }

    public void printNBTEntity(NBTEntity nbtEntity) {
        log.info("NBT values:");
        printNBTCompound(nbtEntity, 0);
    }

    public void printNBTTileEntity(NBTTileEntity nbtTileEntity) {
        log.info("NBT values:");
        printNBTCompound(nbtTileEntity, 0);
    }

    private void printNBTCompound(NBTCompound nbtCompound, int depth) {
        for (String key : nbtCompound.getKeys()) {
            log.info(StringUtils.repeat(" ", depth) + key + " (" + nbtCompound.getType(key).name() + ")");
            depth += 2;
            switch (nbtCompound.getType(key)) {
                case NBTTagByte:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getByte(key));
                    break;
                case NBTTagShort:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getShort(key));
                    break;
                case NBTTagInt:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getInteger(key));
                    break;
                case NBTTagLong:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getLong(key));
                    break;
                case NBTTagFloat:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getFloat(key));
                    break;
                case NBTTagDouble:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getDouble(key));
                    break;
                case NBTTagByteArray:
                    for (byte b : nbtCompound.getByteArray(key)) {
                        log.info(StringUtils.repeat(" ", depth) + "- " + b);
                    }
                    break;
                case NBTTagIntArray:
                    for (int i : nbtCompound.getIntArray(key)) {
                        log.info(StringUtils.repeat(" ", depth) + "- " + i);
                    }
                    break;
                case NBTTagString:
                    log.info(StringUtils.repeat(" ", depth) + nbtCompound.getString(key));
                    break;
                case NBTTagList:
                    printNBTListCompound(nbtCompound.getCompoundList(key), depth);
                    break;
                case NBTTagCompound:
                    printNBTCompound(nbtCompound.getCompound(key), depth);
                    break;
                default:
                    log.warn(StringUtils.repeat(" ", depth) + "(UNKNOWN)");
            }
            depth -= 2;
        }
    }

    private void printNBTListCompound(NBTCompoundList nbtCompoundList, int depth) {
        for (int i = 0; i < nbtCompoundList.size(); i++) {
            NBTListCompound nbtListCompound = nbtCompoundList.get(i);
            log.info(StringUtils.repeat(" ", depth) + "- (" + nbtCompoundList.getType().name() + ")");
            depth += 2;
            for (String key : nbtListCompound.getKeys()) {
                NBTType type = getType(nbtListCompound, key);
                log.info(StringUtils.repeat(" ", depth) + key + " (" + type.name() + ")");
                depth += 2;
                switch (type) {
                    case NBTTagInt:
                        log.info(StringUtils.repeat(" ", depth) + nbtListCompound.getInteger(key));
                        break;
                    case NBTTagDouble:
                        log.info(StringUtils.repeat(" ", depth) + nbtListCompound.getDouble(key));
                        break;
                    case NBTTagString:
                        log.info(StringUtils.repeat(" ", depth) + nbtListCompound.getString(key));
                        break;
                    case NBTTagCompound:
                        throw new UnsupportedOperationException("NBTCompounds in Compound lists aren't supported yet");
                    default:
                        log.warn(StringUtils.repeat(" ", depth) + "(UNKNOWN)");
                }
                depth -= 2;
            }
            depth -= 2;
        }
    }

    private NBTType getType(NBTListCompound nbtListCompound, String key) {
        try {
            Field compoundField = nbtListCompound.getClass().getDeclaredField("compound");
            compoundField.setAccessible(true);
            Object nbtTagCompound = Objects.requireNonNull(ReflectionUtils.getField(compoundField, nbtListCompound));
            val typeId = nbtTagCompound.getClass().getMethod(ReflectionMethod.COMPOUND_GET_TYPE.getMethodName(), String.class).invoke(nbtTagCompound, key);
            if (typeId == null) {
                throw new RuntimeException("Error while getting type from NBTListCompound: type is null");
            }
            return NBTType.valueOf((Byte) typeId);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException("Error while getting type from NBTListCompound key: " + key, e);
        }
    }

}
