package co.bugg.animatedcrosshair.gui;

import co.bugg.animatedcrosshair.AnimatedCrosshair;
import co.bugg.animatedcrosshair.Reference;
import co.bugg.animatedcrosshair.config.ConfigUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * GUI to choose which crosshair is rendered, and a button to customize properties
 */
public class ConfigGui extends GuiScreen {
    /**
     * Array containing all crosshair names
     */
    final ArrayList<String> crosshairDisplayNames = new ArrayList<>();
    /**
     * Name of whatever button is currently selected
     */
    String name;
    /**
     * This string will be prepended in front of the button that displays current crosshair
     */
    String crosshairButtonPrefix = "";

    int buttonWidth = 200;
    int buttonHeight = 20;
    int buttonMargin = 5;

    /**
     * Instantiate with whatever crosshair is currently applied's name as button text
     */
    public ConfigGui() {
        super();
        this.name = AnimatedCrosshair.INSTANCE.config.getCurrentCrosshairName().toUpperCase();
    }

    /**
     * Instantiate with a specific theme on the button
     * @param name Name of the theme to put on the button
     */
    public ConfigGui(String name) {
        this();
        this.name = name.toUpperCase();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(fontRendererObj, Reference.MOD_NAME + " Configuration", width / 2, height / 2 - (buttonHeight + buttonMargin) * 2, 0xFFFFFF);
        drawCenteredString(fontRendererObj, AnimatedCrosshair.INSTANCE.credits, width / 2, height - 10, 0xFFFFFF);
    }

    @Override
    public void initGui() {
        super.initGui();

        final ArrayList<File> crosshairs = getAllPngFiles(new File(ConfigUtil.assetsRoot));

        for(File crosshair : crosshairs) {
            crosshairDisplayNames.add(crosshair.getName().replaceAll("\\.png$", "").toUpperCase());
        }

        int arrowWidth = 20;
        int carouselWidth = buttonWidth - (arrowWidth + buttonMargin) * 2;

        // Add the arrow buttons. They cycle in the direction pressed
        buttonList.add(new GuiButton(0, width / 2 - carouselWidth / 2 - arrowWidth - buttonMargin, height / 2 - buttonHeight / 2 - (buttonHeight + buttonMargin), arrowWidth, buttonHeight, "«"));
        buttonList.add(new GuiButton(1, width / 2 + carouselWidth / 2 + buttonMargin, height / 2 - buttonHeight / 2 - (buttonHeight + buttonMargin), arrowWidth, buttonHeight, "»"));

        // This button doesn't do anything. It simply shows the current theme
        buttonList.add(new GuiButton(2, width / 2 - carouselWidth / 2, height / 2 - buttonHeight / 2 -  (buttonHeight + buttonMargin), carouselWidth, buttonHeight, crosshairButtonPrefix + name));

        // This button pops up the TechnicalGui so you can edit crosshair properties
        buttonList.add(new GuiButton(3, width / 2 - buttonWidth / 2, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight, "Configure"));

        // This button saves the crosshair to be whatever name is on button 0
        buttonList.add(new GuiButton(4, width / 2 - buttonWidth / 2, height / 2 - buttonHeight / 2 + (int) ((buttonHeight + buttonMargin) * 1.5), buttonWidth, buttonHeight,"Save"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        /*
        Button ID 0 = Carousel button
        Button ID 1 = Configure button
        Button ID 2 = Save button
         */

        if(button.id == 0) {
            // Decrement the index to whatever the previous in the ArrayList is
            int currentIndex = crosshairDisplayNames.indexOf(name);
            currentIndex--;

            // If the current index is too low then go to end of list
            if (0 > currentIndex) currentIndex = crosshairDisplayNames.size() - 1;

            // Only implement the decrementation if there is at least 1 crosshair
            if (crosshairDisplayNames.size() > 0) {
                name = crosshairDisplayNames.get(currentIndex);
                buttonList.get(2).displayString = crosshairButtonPrefix + name;
            }
        } else if(button.id == 1) {
            // Increment the index to whatever the next in the ArrayList is
            int currentIndex = crosshairDisplayNames.indexOf(name);
            currentIndex++;

            // If the current index is too high then go back to 0
            if (crosshairDisplayNames.size() < currentIndex + 1) currentIndex = 0;

            // Only implement the incrementation if there is at least 1 crosshair
            if (crosshairDisplayNames.size() > 0) {
                name = crosshairDisplayNames.get(currentIndex);
                buttonList.get(2).displayString = crosshairButtonPrefix + name;
            }
        } else if(button.id == 3) {
            mc.displayGuiScreen(new TechnicalGui(name));
        } else if(button.id == 4) {
            AnimatedCrosshair.INSTANCE.config.setCurrentCrosshairName(name).loadProperties();
            AnimatedCrosshair.INSTANCE.config.save();

            mc.displayGuiScreen(null);
        }
    }

    /**
     * Gets all .png files in the specified folder and returns an ArrayList of them
     * @see "https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java"
     * @param folder Folder to search
     */
    public ArrayList<File> getAllPngFiles(final File folder) {
        final ArrayList<File> fileList = new ArrayList<>();

        if(folder != null && folder.isDirectory()) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    fileList.addAll(getAllPngFiles(fileEntry));
                } else if(Objects.equals(FilenameUtils.getExtension(fileEntry.getName()), "png")){
                    fileList.add(fileEntry);
                }
            }
        }

        return fileList;
    }
}