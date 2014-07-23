/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.nio.file.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Main {
    
    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.out.println("USAGE: java -jar Challenge11.jar feeds_path");
            System.exit(-1);
        }
        final Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            final SortedSet<Event> events = new TreeSet<>();
            final String line[] = sc.nextLine().split(";");
            final int n = Integer.parseInt(line[0].trim());
            for (int i = 1; i < line.length; i++) {
                final String aux[] = line[i].split(",");
                events.addAll(getEvents(args[0], aux[0].trim(), aux[1].trim()));
            }
            final Iterator<Event> it = events.iterator();
            if (it.hasNext()) System.out.print(it.next().id);
            for (int i = 1; i < n; i++) if (it.hasNext()) System.out.print(" " + it.next().id);
            System.out.println();
        }
    }
    
    
    private static Set<Event> getEvents(String root, String userId, String userKey) throws Exception {
        final byte[] encrypted = Files.readAllBytes(Paths.get(root + "/encrypted/" + userId.substring(userId.length()-2) + "/" + userId + ".feed"));
        final byte key[] = Arrays.copyOf(userKey.getBytes("ASCII"), 32); key[29] = key[30] = key[31] = 'a';
        final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        do {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            final String unencrypted = new String(cipher.doFinal(encrypted), "ASCII");
            if (unencrypted.startsWith(userId)) {
                final Set<Event> result = new HashSet<>();
                final Scanner sc = new Scanner(unencrypted);
                while (sc.hasNextLong()) {
                    final long uid = sc.nextLong();
                    final long timestamp = sc.nextLong();
                    final long eventId = sc.nextLong();
                    result.add(new Event(timestamp, eventId));
                }
                return result;
            }
            for (int i = 31; i >= 29; i--) {
                if (key[i] == 'z') {
                    key[i] = 'A';
                    break;
                } else if (key[i] == 'Z') {
                    key[i] = 'a';
                } else {
                    key[i]++;
                    break;
                }
            }
        } while (key[29] != 'a' || key[30] != 'a' || key[31] != 'a');
        return null;
    }
    
    
    private static class Event implements Comparable<Event> {
        
        public final long timestamp, id;
        
        public Event(long timestamp, long id) {
            this.timestamp = timestamp;
            this.id = id;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
            hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Event other = (Event) obj;
            if (this.timestamp != other.timestamp) {
                return false;
            }
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

        @Override
        public int compareTo(Event o) {
            final int aux = Long.compare(o.timestamp, this.timestamp);
            return aux == 0 ? Long.compare(this.id, o.id) : aux;
        }
        
    }
    
}
