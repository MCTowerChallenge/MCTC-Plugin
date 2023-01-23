package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class ListGui extends Gui {

    private static int getInventorySize(int numberOfItems) throws TooManyItemsException {
        if (numberOfItems > 54) {
            throw new TooManyItemsException(numberOfItems);
        }
        return 9 * (((numberOfItems - 1) / 9) + 1);
    }

    private final Element exitElement;
    private final List<Element> elementList;

    public ListGui(Component name, List<Element> elementList, Element exitElement) {
        super(name);
        this.elementList = elementList;
        this.exitElement = exitElement;
        loadInventory();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public ListGui(Component name, Element exitElement) {
        this(name, new ArrayList<>(), exitElement);
    }

    /**
     * Adds an element to the gui
     *
     * @param element element to add
     */
    public void addElement(Element element) {
        this.elementList.add(element);

        loadInventory();
    }

    @Override
    public void loadInventory() {
        int numItems = elementList.size() + (!exitElement.isAir() ? 1 : 0);

        /*

        Less than 54 elements => first page, no page arrows
        More than 54 elements => first page, right page arrow
                                 next pages, left and right page arrows
                                 last page, left page arrow *Blank/grayed out right arrow slot*

         */

        if (numItems > 54) {
            int maxItemsPerPage = 54 - (!exitElement.isAir() ? 1 : 0) - 2;

            int fullPages = elementList.size() / maxItemsPerPage;
            ListPage prevPage = null;
            for (int i = 0; i < fullPages; i++) {
                List<Element> pageElementList = elementList.subList(i * maxItemsPerPage, maxItemsPerPage + (maxItemsPerPage * i));
                ListPage page = new ListPage(pageElementList, false, true, i + 1);
                if (i > 0) {
                    page.setPreviousPage(prevPage);
                    prevPage.setNextPage(page);
                }
                prevPage = page;
            }
            int lastPageItems = elementList.size() - fullPages * maxItemsPerPage;
            List<Element> pageElementList = elementList.subList(fullPages * maxItemsPerPage, lastPageItems + (fullPages * maxItemsPerPage));
            ListPage page = new ListPage(pageElementList, false, true, fullPages + 1);
            if (prevPage != null) {
                prevPage.setNextPage(page);
            }
            page.setPreviousPage(prevPage);
        } else {
            ListPage page = new ListPage(elementList, true, false);
            setFirstInventory(page.getInventory());
        }

    }

    /**
     * Individual page
     */
    public class ListPage {
        private static final Color ARROW_COLOR = Palette.PRIMARY.toBukkitColor();
        private static final Color DISABLED_COLOR = Palette.GRAYED_OUT.toBukkitColor();

        private final List<Element> pageElementList;
        private ListPage previousPage;
        private ListPage nextPage;
        private Inventory inventory;

        /**
         * Whether the GUI automatically adjusts
         * the rows to how many elements are in it
         */
        private final boolean dynamicScale;
        private final boolean showArrows;

        private final int pageNumber;

        public ListPage(List<Element> pageElementList, boolean dynamicScale, boolean showArrows) {
            this(pageElementList, dynamicScale, showArrows, 0);
        }

        public ListPage(List<Element> pageElementList, boolean dynamicScale, boolean showArrows, int pageNumber) {
            this.pageElementList = pageElementList;
            this.dynamicScale = dynamicScale;
            this.showArrows = showArrows;
            this.pageNumber = pageNumber;
            loadInventory();
        }

        public void setNextPage(ListPage nextPage) {
            this.nextPage = nextPage;
            loadInventory();
        }

        public void setPreviousPage(ListPage previousPage) {
            this.previousPage = previousPage;
            loadInventory();
        }

        public Inventory getInventory() {
            return inventory;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        private Element leftArrow() {
            ItemStack arrow = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta = (LeatherArmorMeta) arrow.getItemMeta();
            meta.setCustomModelData(1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            if (previousPage != null) {
                final Component name = TextUtil.noItalic(String.format("Previous Page (Pg. %d)", previousPage.getPageNumber()));
                meta.displayName(name);
                meta.setColor(ARROW_COLOR);
                arrow.setItemMeta(meta);
                return new ButtonElement(arrow, previousPage::openInventory);
            } else {
                final Component name = TextUtil.noItalic("Previous Page");
                meta.displayName(name);
                meta.setColor(DISABLED_COLOR);
                arrow.setItemMeta(meta);
                return new Element(arrow);
            }
        }

        private Element rightArrow() {
            ItemStack arrow = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta = (LeatherArmorMeta) arrow.getItemMeta();
            meta.setCustomModelData(2);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            if (nextPage != null) {
                final Component name = TextUtil.noItalic(String.format("Next Page (Pg. %d)", nextPage.getPageNumber()));
                meta.displayName(name);
                meta.setColor(ARROW_COLOR);
                arrow.setItemMeta(meta);
                return new ButtonElement(arrow, nextPage::openInventory);
            } else {
                final Component name = TextUtil.noItalic("Next Page");
                meta.displayName(name);
                meta.setColor(DISABLED_COLOR);
                arrow.setItemMeta(meta);
                return new Element(arrow);
            }
        }

        public void setFirst() {
            if (previousPage != null) {
                previousPage.setFirst();
            } else {
                setFirstInventory(inventory);
            }
        }

        public void loadInventory() {
            int invSize = 54;
            if (dynamicScale) {
                int numItems = pageElementList.size() + (!exitElement.isAir() ? 1 : 0);
                if (numItems >= 1) {
                    try {
                        invSize = getInventorySize(numItems);
                    } catch (TooManyItemsException e) {
                        Bukkit.getLogger().warning(String.format("%s has been created with too many items: %d items", PlainTextComponentSerializer.plainText().serialize(getInventoryTitle()), numItems));
                    }
                }
            }
            TextComponent.Builder title = Component.text();
            title.append(getInventoryTitle());

            Inventory inventory = Bukkit.createInventory(null, invSize, title.build());

            if (!exitElement.isAir()) {
                registerElement(exitElement.getUUID(), exitElement);
                inventory.setItem(8, exitElement.getItem());
            }

            if (showArrows) {
                Element nextPageArrow = rightArrow();
                Element previousPageArrow = leftArrow();
                registerElement(nextPageArrow.getUUID(), nextPageArrow);
                registerElement(previousPageArrow.getUUID(), previousPageArrow);
                inventory.setItem(inventory.getSize() - 1, nextPageArrow.getItem());
                inventory.setItem(inventory.getSize() - 2, previousPageArrow.getItem());
            }

            for (Element element : pageElementList) {
                registerElement(element.getUUID(), element);
                inventory.addItem(element.getItem());
            }

            List<HumanEntity> viewers = new ArrayList<>();

            if (getInventory() != null) {
                viewers.addAll(getInventory().getViewers());
            }

            setInventory(inventory);
            registerInventory(inventory);
            setFirst();

            for (HumanEntity viewer : viewers) {
                openInventory(viewer);
            }
        }

        public void openInventory(HumanEntity humanEntity) {
            humanEntity.openInventory(inventory);
        }
    }

}

