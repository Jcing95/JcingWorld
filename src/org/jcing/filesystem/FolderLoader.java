package org.jcing.filesystem;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.jcing.jcingworld.logging.Logs;

public class FolderLoader {

    private boolean indexedLoad = false;
    public static final String FOLDER_INDEX_FILENAME = "iidx.jll";

    private static PrintStream out = Logs.fileLoader;

    /**
     * loads all Images in a Folder and indexes them.<br>
     * Not yet indexed images will be added at the end.
     */
    @SuppressWarnings("unchecked")
    public LinkedList<BufferedImage> indexedLoad(String folderPath) {
        out.println("loading images of Folder [" + folderPath + "]");
        LinkedList<String> filepaths;
        File imageIndex = new File(folderPath + "/loadata.Jdta");
        if (imageIndex.exists()) {
            out.println(FOLDER_INDEX_FILENAME + " exists!");
            try {
                FileInputStream fis = new FileInputStream(imageIndex);
                ObjectInputStream ois = new ObjectInputStream(fis);
                filepaths = (LinkedList<String>) ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException e) {
                System.err.println("Could not load " + FOLDER_INDEX_FILENAME + " - IO problem!");
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                System.err.println("Could not load " + FOLDER_INDEX_FILENAME + " - not found!");
                e.printStackTrace();
                return null;
            } catch (ClassCastException e) {
                System.err.println(
                        "Could not load " + FOLDER_INDEX_FILENAME + " - probably corrupted!");
                e.printStackTrace();
                return null;
            }
        } else {
            out.println(FOLDER_INDEX_FILENAME + " does not exist - will be created!");
            filepaths = new LinkedList<String>();
        }
        indexedLoad = false;
        LinkedList<BufferedImage> imgs = indexSubLoad(new LinkedList<BufferedImage>(), filepaths,
                "res/" + folderPath);

        indexedLoad = false;
        return imgs;
    }

    public static Object loadFile(File file) {
        Object obj = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load Object at: " + file.getPath());
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    public static Object loadFileInJar(String path) {
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FolderLoader().getClass().getClassLoader().getResourceAsStream(path));
            obj = ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load injar Object at: " + path);
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    public static BufferedImage loadImage(File file) {
        BufferedImage img = null;
        try {
            out.println("loading: " + file);
            img = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Could not Load Image: " + file.getPath());
            e.printStackTrace();
            return null;
        }
        return img;
    }

    public static void saveImage(String path, RenderedImage img) {
        File file = new File(path);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            System.err.println("Could not save img at: " + path);
            e.printStackTrace();
        }
    }

    private LinkedList<BufferedImage> indexSubLoad(LinkedList<BufferedImage> imgs,
            LinkedList<String> filepaths, String folderPath) {
        File location = new File(folderPath);
        if (!location.isDirectory()) {
            System.err.println("IMAGE HANDLER ERROR: path is no directory!");
        } else {
            if (!indexedLoad) {
                indexedLoad = true;
                for (int i = 0; i < filepaths.size(); i++) {
                    File file = new File(filepaths.get(i));
                    if (file.exists()) {
                        out.println("Indexed img: ");
                        if (file.isDirectory()) {

                        } else {
                            imgs.add(loadImage(file));
                        }
                    } else {
                        out.println("Missing: " + file.getPath());
                        imgs.add(imgs.getFirst());
                    }
                }
            }
            String[] files = location.list();
            for (int i = 0; i < files.length; i++) {
                File file = new File(folderPath + "/" + files[i]);
                if (filepaths.contains(file.getPath())) {
                } else {
                    out.println(file.getName());

                    if (file.isDirectory() || file.getName()
                            .substring(file.getName().lastIndexOf('.')).equals(".png")) {
                        filepaths.add(file.getPath());
                        out.println("Adding new file index: " + file.getPath());
                        if (file.isDirectory()) {
                            //                            imgs.add(new JCImage(file.getPath(), true));
                        } else {
                            imgs.add(loadImage(file));
                        }

                        out.println("loaded file!");
                    }

                }
            }
        }
        return imgs;
    }

    // /**
    //  * loads all n.pngs in a Folder. used primarily for animations.
    //  * 
    //  * @param path
    //  * @return
    //  */
    // public static ArrayList<BaseImage> numericLoad(final String path) {
    //     // TODO: make sure that every file is <Integer>.png
    //     ArrayList<BaseImage> imgs = new ArrayList<BaseImage>();
    //     File location = new File(path);
    //     if(!location.isDirectory()){
    //         imgs.add(new BaseImage(path));
    //         return imgs;
    //     }
    //     String[] files = location.list();
    //     Arrays.sort(files, new Comparator<String>() {
    //         public int compare(String o1, String o2) {
    //             if (new File(path + "/" + o1).isDirectory() || new File(path + "/" + o2).isDirectory()) {
    //                 return 0;
    //             }
    //             return Integer.valueOf(o1.substring(0, o1.indexOf(".")))
    //                     .compareTo(Integer.valueOf(o2.substring(0, o2.indexOf("."))));
    //         }
    //     });
    //     for (int i = 0; i < files.length; i++) {
    //         if (!new File(path + "/" + files[i]).isDirectory()) {
    //             o.println("numeric Load: " + files[i]);
    //             imgs.add(new JCImage(path + "/" + files[i]));
    //         }
    //     }
    //     return imgs;
    // }

}
