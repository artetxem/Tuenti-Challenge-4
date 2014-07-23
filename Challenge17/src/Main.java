/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

public class Main {
    
    private static final long BYTES_PER_SECTOR = 512;
    
    private static MessageDigest md5;
    private static RandomAccessFile dump;
    private static long partitionBegin, sectorsPerCluster, reservedSectors, fats, sectorsPerFat, rootDirectoryFirstCluster, fatBegin, clusterBegin;
    
    public static void main(String args[]) throws NoSuchAlgorithmException, IOException {
        if (args.length != 1) {
            System.out.println("USAGE: java -jar Challenge17.jar FAT32DUMP.BIN");
            System.exit(-1);
        }
        try {
            md5 = MessageDigest.getInstance("MD5");
            dump = new RandomAccessFile(args[0], "r");
            partitionBegin = read32(454)*BYTES_PER_SECTOR;
            sectorsPerCluster = read8(partitionBegin+0x0d);
            reservedSectors = read16(partitionBegin+0x0e);
            fats = read8(partitionBegin+0x10);
            sectorsPerFat = read32(partitionBegin+0x24);
            rootDirectoryFirstCluster = read32(partitionBegin+0x2c);
            fatBegin = partitionBegin + reservedSectors*BYTES_PER_SECTOR;
            clusterBegin = partitionBegin + (reservedSectors+(fats*sectorsPerFat))*BYTES_PER_SECTOR;
            final Scanner sc = new Scanner(System.in);
            final int n = sc.nextInt(); sc.nextLine();
            for (int it = 0; it < n; it++) {
                final String path[] = sc.nextLine().trim().substring(1).split("/");
                Entry currentEntry = Entry.rootDirectory();
                boolean found = true;
                for (int i = 0; i < path.length && currentEntry.isDirectory && found; i++) {
                    found = false;
                    for (Entry entry : readDirectory(currentEntry.firstCluster)) {
                        if (entry.name.equalsIgnoreCase(path[i])) {
                            found = true;
                            currentEntry = entry;
                            break;
                        }
                    }
                }
                if (!found || currentEntry.isDirectory) {
                    // var cenas;
                    System.out.println("CORRUPT");
                } else {
                    final String hash = fileHash(currentEntry);
                    System.out.println(hash == null ? "CORRUPT" : hash);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + args[0]);
            System.exit(-1);
        }
    }
    
    private static byte[] readBytes(long index, int count) throws IOException {
        final byte[] result = new byte[count];
        dump.seek(index);
        dump.read(result);
        return result;
    }
    
    private static long read8(long index) throws IOException {
        dump.seek(index);
        return dump.readUnsignedByte();
    }
    
    private static long read16(long index) throws IOException {
        dump.seek(index);
        return dump.readUnsignedByte() + 256L*dump.readUnsignedByte();
    }
    
    private static long read32(long index) throws IOException {
        dump.seek(index);
        return dump.readUnsignedByte() + 256L*dump.readUnsignedByte() + 65536L*dump.readUnsignedByte() + 16777216L*dump.readUnsignedByte();
    }
    
    private static Long nextCluster(long cluster) throws IOException { // Returns null if last cluster
        final long nextCluster = read32(fatBegin+cluster*4) & 0x0FFFFFFF; // The top 4 bits shouldn't be taken into account
        return nextCluster >= 268435448L ? null : nextCluster; // Last cluster check
    }
    
    private static List<Entry> readDirectory(long firstCluster) throws IOException {
        final List<Entry> list = new ArrayList<>();
        for (Long cluster = firstCluster; cluster != null; cluster = nextCluster(cluster)) {
            for (long offset = 0; offset < sectorsPerCluster*BYTES_PER_SECTOR; offset+=32) {
                final long begin = clusterBegin+(cluster-2)*sectorsPerCluster*BYTES_PER_SECTOR+offset;
                final long firstByte = read8(begin);
                if (firstByte == 229){} // Unused entry
                else if (firstByte == 0) return list; // End of directory
                else list.add(new Entry(begin));
            }
        }
        return list;
    }
    
    private static String fileHash(Entry entry) throws IOException { // Returns null if corrupt
        final Set<Long> visitedClusters = new HashSet<>();
        md5.reset();
        long read = 0;
        Long cluster = entry.firstCluster;
        while (cluster != null && read < entry.size) {
            if (!visitedClusters.add(cluster)) return null; // We found a cycle!
            final long begin = clusterBegin+(cluster-2)*sectorsPerCluster*BYTES_PER_SECTOR;
            final int count = (int)Math.min(entry.size-read,(sectorsPerCluster*BYTES_PER_SECTOR));
            md5.update(readBytes(begin, count));
            read += count;
            cluster = nextCluster(cluster);
        }
        if (read < entry.size || cluster != null) return null; // Mismatch in the file size!
        String hash = new BigInteger(1, md5.digest()).toString(16);
        while (hash.length() < 32 ) hash = "0" + hash;
        return hash;
    }
    
    
    
    private static class Entry {
        
        public final String name;
        public final boolean isDirectory;
        public final long firstCluster;
        public final long size;
        
        public static Entry rootDirectory() {
            return new Entry("", true, rootDirectoryFirstCluster, 0);
        }
        
        private Entry(String name, boolean isDirectory, long firstCluster, long size) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.firstCluster = firstCluster;
            this.size = size;
        }
        
        public Entry(long begin) throws IOException {
            final String filename = new String(readBytes(begin,8), "ASCII").trim();
            final String extension = new String(readBytes(begin+8,3), "ASCII").trim();
            name = extension.isEmpty() ? filename : filename + "." + extension;
            isDirectory = (read8(begin+0x0b) & 0x10) != 0;
            firstCluster = 65536*read16(begin+0x14) + read16(begin+0x1a);
            size = read32(begin+0x1c);
        }
    }
    
}
