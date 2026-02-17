package de.lorberry.arsen.analysis;

import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.util.Logger;

public class EntryPointAnalyzer {

    public Long findEntryPoint(ExecutableFile file) {
        long entryPoint = file.getEntryPoint();
        long imageBase = file.getImageBase();
        long absoluteEntry = imageBase + entryPoint;

        Logger.info("Entry point: 0x" + Long.toHexString(entryPoint));
        Logger.info("Image base: 0x" + Long.toHexString(imageBase));
        Logger.info("Absolute entry: 0x" + Long.toHexString(absoluteEntry));

        return absoluteEntry;
    }
}
