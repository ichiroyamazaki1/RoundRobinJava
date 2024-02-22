package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Scanner;

public class RoundRobin extends Application {

    private void drawGanttChart(int num, int[] startTimes, int[] finishTimes) {
        launchGanttChartWindow(startTimes, finishTimes);
    }

    private void launchGanttChartWindow(int[] startTimes, int[] finishTimes) {
        Stage primaryStage = new Stage();
        Pane root = new Pane();
        double scale = 50.0;

        Text title = new Text("Gantt Chart Table");
        title.setFont(new Font("Times New Roman", 20));
        title.setX(30);
        title.setY(30);

        Text totalTimeText = new Text("Total Time: " + calculateTotalTime(finishTimes));
        totalTimeText.setFont(new Font("Times New Roman", 12));
        totalTimeText.setX(30);
        totalTimeText.setY(120);

        root.getChildren().addAll(title, totalTimeText /* ... (Additional information text) */);

        double xPos = 0;
        for (int i = 0; i < finishTimes.length; i++) {
            double width = (i == 0 ? finishTimes[i] : finishTimes[i] - finishTimes[i - 1]) * scale;

            Rectangle rect = new Rectangle(xPos, 50, width, 20);
            rect.setStroke(Color.BLACK);
            rect.setFill(Color.TRANSPARENT);

            Text text = new Text(xPos + 5, 65, "P" + (i + 1));

            root.getChildren().addAll(rect, text);
            xPos += width;
        }

        for (int i = 0; i <= calculateTotalTime(finishTimes); i++) {
            Text timeText = new Text(scale * i - (i < 10 ? 3 : 7), 85, String.valueOf(i));
            root.getChildren().add(timeText);
        }

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setPrefViewportWidth(600);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setPannable(true);

        primaryStage.setTitle("Gantt Chart - Round Robin");
        primaryStage.setScene(new Scene(scrollPane));
        primaryStage.show();
    }

    private int calculateTotalTime(int[] finishTimes) {
        int totalTime = 0;
        for (int finishTime : finishTimes) {
            totalTime = Math.max(totalTime, finishTime);
        }
        return totalTime;
    }

    private void roundRobinScheduling() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("--------------------------");
            System.out.println("       ROUND ROBIN ");
            System.out.println("--------------------------");
            System.out.print("Enter the number of processes: ");
            int num = input.nextInt();

            int[] burst = new int[num];
            int[] arrival = new int[num];
            int[] finish = new int[num];
            int[] wait = new int[num];
            int[] turnaround = new int[num];
            for (int i = 0; i < num; i++) {
                System.out.print("Enter the burst time for p" + (i + 1) + ": ");
                burst[i] = input.nextInt();
                System.out.print("Enter the arrival time for p" + (i + 1) + ": ");
                arrival[i] = input.nextInt();
            }

            System.out.print("Enter the quantum number: ");
            int quantum = input.nextInt();

            int currentTime = 0;

            int[] startTimes = new int[num];

            do {
                boolean allFinished = true;

                for (int i = 0; i < num; i++) {
                    if (burst[i] > 0 && arrival[i] <= currentTime) {
                        allFinished = false;

                        int executeTime = Math.min(burst[i], quantum);
                        finish[i] = currentTime + executeTime;
                        currentTime += executeTime;
                        burst[i] -= executeTime;

                        for (int j = 0; j < num; j++) {
                            if (j != i && burst[j] > 0) {
                                wait[j] += executeTime;
                            }
                        }

                        if (startTimes[i] == 0) {
                            startTimes[i] = currentTime - executeTime;
                        }
                    }
                }

                if (allFinished) {
                    break;
                }
            } while (true);

            System.out.println("Process\tArrival\tBurst\tFinish\tTurnaround\tWaiting");
            float totalTurnaround = 0;
            float totalWait = 0;

            for (int i = 0; i < num; i++) {
                turnaround[i] = finish[i] - arrival[i];
                totalTurnaround += turnaround[i];
                totalWait += wait[i];

                System.out.println("p" + (i + 1) + "\t" + arrival[i] + "\t\t" + burst[i] + "\t" + finish[i] + "\t" +
                        turnaround[i] + "\t\t" + wait[i]);
            }

            System.out.println("\nAverage turnaround time is: " + (totalTurnaround / num));
            System.out.println("Average waiting time is: " + (totalWait / num));

            // Gantt chart
            System.out.println("\nGantt Chart:");
            drawGanttChart(num, startTimes, finish);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        roundRobinScheduling();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
