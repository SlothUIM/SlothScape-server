package server.model.players.content;

import java.util.*;

import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;

public class GodBookManager {

    public static class GodBook {
        public final int damagedBookId;
        public final int completedBookId;
        public final int[] pageIds;

        public GodBook(int damagedBookId, int completedBookId, int[] pageIds) {
            this.damagedBookId = damagedBookId;
            this.completedBookId = completedBookId;
            this.pageIds = pageIds;
        }

        public boolean isPage(int itemId) {
            for (int id : pageIds) {
                if (id == itemId) return true;
            }
            return false;
        }
    }

    private static final Map<Integer, GodBook> bookByDamagedId = new HashMap<>();
    private static final Map<Integer, GodBook> pageToBook = new HashMap<>();

    static {
        registerBook(new GodBook(3839, 3840, new int[]{3827, 3828, 3829, 3830})); // Holy
        registerBook(new GodBook(3841, 3842, new int[]{3831, 3832, 3833, 3834})); // Unholy
        registerBook(new GodBook(3843, 3844, new int[]{3835, 3836, 3837, 3838})); // Balance
        // Add more books here
    }

    private static void registerBook(GodBook book) {
        bookByDamagedId.put(book.damagedBookId, book);
        for (int pageId : book.pageIds) {
            pageToBook.put(pageId, book);
        }
    }

    public static boolean handleBookPageUse(int itemUsed, int useWith, Player c) {
        int bookId = (bookByDamagedId.containsKey(itemUsed)) ? itemUsed :
                     (bookByDamagedId.containsKey(useWith)) ? useWith : -1;
        int pageId = (pageToBook.containsKey(itemUsed)) ? itemUsed :
                     (pageToBook.containsKey(useWith)) ? useWith : -1;

        if (bookId == -1 || pageId == -1) return false;

        GodBook book = bookByDamagedId.get(bookId);
        if (book == null || !book.isPage(pageId)) return false;

        Set<Integer> addedPages = getAddedPages(c, book.damagedBookId);

        // Check if page was already added
        if (addedPages.contains(pageId)) {
            c.sendMessage("You've already added this page to the book.");
            return true;
        }

        if (!c.getItems().playerHasItem(book.damagedBookId, 1) || !c.getItems().playerHasItem(pageId, 1)) {
            c.sendMessage("You need both the damaged book and the page to continue.");
            return true;
        }

        c.getItems().deleteItem(pageId, c.getItems().getItemSlot(pageId), 1);
        c.getItems().deleteItem(book.damagedBookId, 1);
        


        addedPages.add(pageId); // Only count this page once

        if (addedPages.size() == 4) {
            c.getItems().addItem(book.completedBookId, 1);
            c.sendMessage("You’ve added the final page and completed the book!");
        } else {
            c.getItems().addItem(book.damagedBookId, 1);
            c.sendMessage("You add a page to the book. Pages added: " + addedPages.size() + "/4.");
        }

        return true;
    }


    public static Map<Integer, Set<Integer>> getAllBookProgress(Player p) {
        return playerPagesAdded.computeIfAbsent(p, k -> new HashMap<>());
    }

    public static void markPageAdded(Player p, int bookId, int pageId) {
        getAllBookProgress(p).computeIfAbsent(bookId, k -> new HashSet<>()).add(pageId);
    }

    public static int getPageCountForBook(Player c, int bookId) {
        Set<Integer> pages = getAllBookProgress(c).get(bookId);
        return pages != null ? pages.size() : 0;
    }

    // Example tracking system: per-player book progress (replace with persistent storage if needed)
    private static final Map<Player, Map<Integer, Set<Integer>>> playerPagesAdded = new HashMap<>();



    private static Set<Integer> getAddedPages(Player c, int bookId) {
        return playerPagesAdded
                .computeIfAbsent(c, k -> new HashMap<>())
                .computeIfAbsent(bookId, k -> new HashSet<>());
    }
    public static boolean isPreachableBook(int itemId) {
        for (GodBook book : bookByDamagedId.values()) {
            if (book.completedBookId == itemId) return true;
        }
        return false;
    }

    public static void openPreachMenu(Player c, int bookId) {
        c.preachingBook = bookId; // Store book ID for later action
        c.getDH().sendOption4("Wedding Ceremony", "Last Rites", "Blessing", "Preach");
    }

    public static void handlePreachOption(Player c, int bookId, int buttonId) {
        int prayerCost = 10;
        if (c.getSkills().getLevel(Skill.PRAYER) < prayerCost) {
            c.sendMessage("You need at least " + prayerCost + " Prayer to preach.");
            return;
        }
        c.getSkills().decreaseLevel(prayerCost, Skill.PRAYER);
        c.getSkills().sendRefresh(Skill.PRAYER);
        c.startAnimation(1670);


        List<String> lines = getPreachLines(bookId, buttonId);

        if (lines == null || lines.isEmpty()) {
            c.sendMessage("You attempt to preach, but nothing comes out.");
            return;
        }

        c.preachQueue.clear();
        c.preachQueue.addAll(lines);
        c.isPreaching = true;
        c.preachTicks = 0;

    }//7154, 7155, 7153 are bandos, armdyl, and ancient god book animations
    public static List<String> getPreachLines(int bookId, int buttonId) {
        return switch (bookId) {
            case 3840 -> getSaradominLines(buttonId);
            case 3842 -> getZamorakLines(buttonId);
            case 3844 -> getGuthixLines(buttonId);
            default -> null;
        };
    }

    private static List<String> getSaradominLines(int buttonId) {
        return switch (buttonId) {
            case 9178 -> List.of("In the name of Saradomin,", "protector of us all,", "I now join you in the eyes of Saradomin.");
            case 9179 -> List.of("Thy cause was false,", "thy skills did lack;", "See you in Lumbridge when you get back.");
            case 9180 -> List.of("Go in peace in the name of Saradomin;", "May his glory shine upon you like the sun.");
            case 9181 -> List.of("The currency of goodness is honour;", "It retains its value through scarcity.", "This is Saradomin's wisdom.");
            default -> null;
        };
    }

    private static List<String> getZamorakLines(int buttonId) {
        return switch (buttonId) {
            case 9178 -> List.of("Two great warriors,", "joined by hand,", "to spread destruction across the land.", "In Zamorak's name, now two are one.");
            case 9179 -> List.of("The weak deserve to die,", "so the strong may flourish.", "This is the creed of Zamorak.");
            case 9180 -> List.of("May your bloodthirst never be sated,", "and may all your battles be glorious.", "Zamorak bring you strength.");
            case 9181 -> {
                List<List<String>> variants = List.of(
                    List.of("There is no opinion that cannot be proven true...", "by crushing those who choose to disagree with it.", "Zamorak give me strength!"),
                    List.of("Battles are not lost and won;", "They simply remove the weak from the equation.", "Zamorak give me strength!"),
                    List.of("Those who fight, then run away,", "shame Zamorak with their cowardice.", "Zamorak give me strength!"),
                    List.of("Strike fast, strike hard, strike true:", "The strength of Zamorak will be with you.", "Zamorak give me strength!")
                );
                yield variants.get(Misc.random(variants.size() - 1));
            }
            default -> null;
        };
    }

    private static List<String> getGuthixLines(int buttonId) {
        return switch (buttonId) {
            case 9178 -> List.of("Light and dark, day and night,", "balance arises from contrast.", "I unify thee in the name of Guthix.");
            case 9179 -> List.of("Thy death was not in vain,", "for it brought some balance to the world.", "May Guthix bring you rest.");
            case 9180 -> List.of("May you walk the path, and never fall,", "for Guthix walks beside thee on thy journey.", "May Guthix bring you peace.");
            case 9181 -> List.of("The trees, the earth, the sky, the waters;", "All play their part upon this land.", "May Guthix bring you balance.");
            default -> null;
        };
    }



}
