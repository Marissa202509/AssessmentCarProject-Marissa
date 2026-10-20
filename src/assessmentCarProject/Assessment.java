//Marissa 10/15/26
package assessmentCarProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assessment {

	private static boolean isSorted = false;

	// Load car records from the CSV file
	public static ArrayList<Car> loadCars(String filename) {
		ArrayList<Car> cars = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String line = br.readLine(); // Skip header row

			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] parts = line.split(",");

				if (parts.length >= 7) {
					try {
						String id = parts[0].trim();
						String brand = parts[1].trim();
						String model = parts[2].trim();
						int year = Integer.parseInt(parts[3].trim());
						String fuel = parts[4].trim();
						String color = parts[5].trim();
						double mileage = Double.parseDouble(parts[6].trim());

						cars.add(new Car(id, brand, model, year, fuel, color, mileage));

					} catch (NumberFormatException e) {
						System.out.println("Skipping row with invalid numeric data.");
					}
				}
			}

			System.out.println("Total cars loaded: " + cars.size());

		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage());
		}

		return cars;
	}

	// Sort cars alphabetically by brand using selection sort
	public static void selectionSortByBrand(ArrayList<Car> list) {
		int n = list.size();

		for (int i = 0; i < n - 1; i++) {
			int minIdx = i;

			for (int j = i + 1; j < n; j++) {
				if (list.get(j).getBrand().compareToIgnoreCase(list.get(minIdx).getBrand()) < 0) {
					minIdx = j;
				}
			}

			if (minIdx != i) {
				Car temp = list.get(i);
				list.set(i, list.get(minIdx));
				list.set(minIdx, temp);
			}
		}

		isSorted = true;
	}

	// Find all cars matching a brand using binary search
	public static ArrayList<Car> binarySearchByBrand(ArrayList<Car> list, String targetBrand) {

		ArrayList<Car> matches = new ArrayList<>();

		int low = 0;
		int high = list.size() - 1;
		int foundIndex = -1;

		while (low <= high) {
			int mid = low + (high - low) / 2;

			int cmp = list.get(mid).getBrand().compareToIgnoreCase(targetBrand);

			if (cmp == 0) {
				foundIndex = mid;
				break;
			} else if (cmp < 0) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}

		if (foundIndex != -1) {
			// Find the first matching brand
			int left = foundIndex;

			while (left > 0 && list.get(left - 1).getBrand().equalsIgnoreCase(targetBrand)) {
				left--;
			}

			// Collect every matching car
			int right = foundIndex;

			while (right < list.size() && list.get(right).getBrand().equalsIgnoreCase(targetBrand)) {
				matches.add(list.get(right));
				right++;
			}
		}

		return matches;
	}

	// Display average mileage and counts by fuel type
	public static void displayStatistics(ArrayList<Car> list) {
		if (list.isEmpty()) {
			System.out.println("No data available for statistics.");
			return;
		}

		double totalMileage = 0;
		Map<String, Integer> fuelCounts = new HashMap<>();

		for (Car car : list) {
			totalMileage += car.getMileageKmpl();

			String fuel = car.getFuelType();
			fuelCounts.put(fuel, fuelCounts.getOrDefault(fuel, 0) + 1);
		}

		double avgMileage = totalMileage / list.size();

		System.out.printf("Average Mileage: %.2f km/l%n", avgMileage);

		System.out.println("Car counts by fuel type:");

		for (Map.Entry<String, Integer> entry : fuelCounts.entrySet()) {
			System.out.printf(" - %s: %d%n", entry.getKey(), entry.getValue());
		}
	}

	// Print the first 10 cars
	public static void printFirst10(ArrayList<Car> list) {
		System.out.println("\n--- First 10 Cars ---");

		int limit = Math.min(10, list.size());

		for (int i = 0; i < limit; i++) {
			System.out.println(list.get(i));
		}

		System.out.println();
	}

	public static void main(String[] args) {
		ArrayList<Car> cars = loadCars("Car_data.csv");

		if (cars.isEmpty()) {
			System.out.println("No cars were loaded. Check the CSV file.");
			return;
		}

		// Analyze up to 2,000 cars, or all cars if fewer are available
		int limit = Math.min(2000, cars.size());
		ArrayList<Car> working = new ArrayList<>(cars.subList(0, limit));

		Scanner scanner = new Scanner(System.in);
		int choice = 0;

		while (choice != 4) {
			System.out.println("\n=== Car Data Analyzer Menu ===");
			System.out.println("1. Sort cars by Brand");
			System.out.println("2. Search for cars by Brand");
			System.out.println("3. Display statistics");
			System.out.println("4. Exit");
			System.out.print("Enter your choice (1-4): ");

			if (!scanner.hasNextInt()) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
				continue;
			}

			choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			switch (choice) {
			case 1:
				System.out.println("Sorting cars by brand...");
				selectionSortByBrand(working);
				System.out.println("Sorting completed successfully!");
				printFirst10(working);
				break;

			case 2:
				if (!isSorted) {
					System.out.println("Sorting is required before binary search.");
					selectionSortByBrand(working);
				}

				System.out.print("Enter brand name to search: ");
				String searchBrand = scanner.nextLine().trim();

				ArrayList<Car> results = binarySearchByBrand(working, searchBrand);

				if (results.isEmpty()) {
					System.out.println("No cars found matching brand: '" + searchBrand + "'");
				} else {
					System.out.println("Found " + results.size() + " matching car(s):");

					for (Car car : results) {
						System.out.println(car);
					}
				}
				break;

			case 3:
				displayStatistics(working);
				break;

			case 4:
				System.out.println("Exiting program. Goodbye!");
				break;

			default:
				System.out.println("Invalid selection. Please enter 1, 2, 3, or 4.");
			}
		}

		scanner.close();
	}
}