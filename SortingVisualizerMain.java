import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SortingVisualizerMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortingVisualizerMain::createAndShowMainGUI);
    }

    private static void createAndShowMainGUI() {
        JFrame mainFrame = new JFrame("Sorting Visualizer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(0, 1));

        JButton bubbleSortButton = new JButton("Bubble Sort");
        JButton quickSortButton = new JButton("Quick Sort");
        JButton mergeSortButton = new JButton("Merge Sort");
        JButton selectionSortButton = new JButton("Selection Sort");
        JButton insertionSortButton = new JButton("Insertion Sort");

        bubbleSortButton.addActionListener(e -> createAndShowGUI("Bubble Sort", new BubbleSortVisualizer(new int[]{5, 3, 8, 4, 2, 7, 1, 10, 9, 6})));
        quickSortButton.addActionListener(e -> createAndShowGUI("Quick Sort", new QuickSortVisualizer(new int[]{5, 3, 8, 4, 2, 7, 1, 10, 9, 6})));
        mergeSortButton.addActionListener(e -> createAndShowGUI("Merge Sort", new MergeSortVisualizer(new int[]{5, 3, 8, 4, 2, 7, 1, 10, 9, 6})));
        selectionSortButton.addActionListener(e -> createAndShowGUI("Selection Sort", new SelectionSortVisualizer(new int[]{5, 3, 8, 4, 2, 7, 1, 10, 9, 6})));
        insertionSortButton.addActionListener(e -> createAndShowGUI("Insertion Sort", new InsertionSortVisualizer(new int[]{5, 3, 8, 4, 2, 7, 1, 10, 9, 6})));

        mainFrame.add(bubbleSortButton);
        mainFrame.add(quickSortButton);
        mainFrame.add(mergeSortButton);
        mainFrame.add(selectionSortButton);
        mainFrame.add(insertionSortButton);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private static void createAndShowGUI(String title, SortingVisualizer visualizer) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(visualizer);
        frame.pack();
        frame.setVisible(true);

        new Thread(visualizer::startSorting).start();
    }
}

abstract class SortingVisualizer extends JPanel {
    protected int[] array;
    private int[] originalArray;
    private long startTime;
    private JLabel timerLabel;

    public SortingVisualizer(int[] array) {
        this.array = array;
        this.originalArray = Arrays.copyOf(array, array.length);
        setPreferredSize(new Dimension(800, 600));
        timerLabel = new JLabel("Time: 0 ms");
        add(timerLabel);
    }

    public void resetArray() {
        this.array = Arrays.copyOf(originalArray, originalArray.length);
        repaint();
    }

    public void startSorting() {
        resetArray();
        startTime = System.currentTimeMillis();
        sort();
        long endTime = System.currentTimeMillis();
        timerLabel.setText("Time: " + (endTime - startTime) + " ms");
    }

    protected abstract void sort();

    protected void repaintAndSleep() {
        repaint();
        try {
            Thread.sleep(50 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight() - 50; // Reserve space for the timer
        int barWidth = width / array.length;
        int maxVal = Arrays.stream(array).max().getAsInt();

        for (int i = 0; i < array.length; i++) {
            int barHeight = (int) (((double) array[i] / maxVal) * height);
            g.fillRect(i * barWidth, height - barHeight, barWidth, barHeight);
        }
    }
}

class BubbleSortVisualizer extends SortingVisualizer {
    public BubbleSortVisualizer(int[] array) {
        super(array);
    }

    @Override
    protected void sort() {
        int n = array.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                    repaintAndSleep();
                }
            }
            if (!swapped) break;
        }
    }
}

class QuickSortVisualizer extends SortingVisualizer {
    public QuickSortVisualizer(int[] array) {
        super(array);
    }

    @Override
    protected void sort() {
        quickSort(0, array.length - 1);
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            repaintAndSleep();
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                repaintAndSleep();
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        repaintAndSleep();
        return i + 1;
    }
}

class MergeSortVisualizer extends SortingVisualizer {
    public MergeSortVisualizer(int[] array) {
        super(array);
    }

    @Override
    protected void sort() {
        mergeSort(0, array.length - 1);
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
            repaintAndSleep();
        }
    }

    private void merge(int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }
}

class SelectionSortVisualizer extends SortingVisualizer {
    public SelectionSortVisualizer(int[] array) {
        super(array);
    }

    @Override
    protected void sort() {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[min_idx]) {
                    min_idx = j;
                }
            }
            int temp = array[min_idx];
            array[min_idx] = array[i];
            array[i] = temp;
            repaintAndSleep();
        }
    }
}

class InsertionSortVisualizer extends SortingVisualizer {
    public InsertionSortVisualizer(int[] array) {
        super(array);
    }

    @Override
    protected void sort() {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
                repaintAndSleep();
            }
            array[j + 1] = key;
            repaintAndSleep();
        }
    }
}
