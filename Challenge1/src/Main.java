/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.util.*;

public class Main {
    
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("USAGE: java -jar Challenge1.jar students.txt");
            System.exit(-1);
        }
        try {
            final Map<Profile,SortedSet<String>> profileToNames = parseProfiles(new File(args[0]));
            final Scanner sc = new Scanner(System.in);
            final int n = sc.nextInt(); sc.nextLine();
            for (int it = 1; it <= n; it++) {
                final String[] data = sc.nextLine().split(",");
                if (data.length == 4) {
                    final Profile profile = new Profile(data[0], data[1], data[2], data[3]);
                    final SortedSet<String> names = profileToNames.get(profile);
                    System.out.print("Case #" + it + ": ");
                    if (names == null) {
                        System.out.println("NONE");
                    } else {
                        final Iterator<String> nameIterator = names.iterator();
                        System.out.print(nameIterator.next());
                        while (nameIterator.hasNext()) System.out.print("," + nameIterator.next());
                        System.out.println();
                    }
                }
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + args[0]);
            System.exit(-1);
        }
    }
    
    private static Map<Profile,SortedSet<String>> parseProfiles(File file) throws FileNotFoundException {
        final Map<Profile,SortedSet<String>> profileToNames = new HashMap<Profile,SortedSet<String>>();
        final Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            final String[] data = sc.nextLine().split(",");
            if (data.length == 5) {
                final Profile profile = new Profile(data[1], data[2], data[3], data[4]);
                SortedSet<String> names = profileToNames.get(profile);
                if (names == null) {
                    names = new TreeSet<String>();
                    profileToNames.put(profile, names);
                }
                names.add(data[0]);
            }
        }
        return profileToNames;
    }
    
    
    private static class Profile {
        
        public final String gender, age, studies, academicYear;
        
        public Profile(String gender, String age, String studies, String academicYear) {
            this.gender = gender;
            this.age = age;
            this.studies = studies;
            this.academicYear = academicYear;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.gender != null ? this.gender.hashCode() : 0);
            hash = 37 * hash + (this.age != null ? this.age.hashCode() : 0);
            hash = 37 * hash + (this.studies != null ? this.studies.hashCode() : 0);
            hash = 37 * hash + (this.academicYear != null ? this.academicYear.hashCode() : 0);
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
            final Profile other = (Profile) obj;
            if ((this.gender == null) ? (other.gender != null) : !this.gender.equals(other.gender)) {
                return false;
            }
            if ((this.age == null) ? (other.age != null) : !this.age.equals(other.age)) {
                return false;
            }
            if ((this.studies == null) ? (other.studies != null) : !this.studies.equals(other.studies)) {
                return false;
            }
            if ((this.academicYear == null) ? (other.academicYear != null) : !this.academicYear.equals(other.academicYear)) {
                return false;
            }
            return true;
        }
        
    }
    
}
