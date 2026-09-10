import Foundation

public struct AutoCalculators {

    public static func calculateDailySafeSpend(
        monthlyBudget: Int,
        totalSpent: Int,
        daysInMonth: Int,
        currentDay: Int
    ) -> Int {
        let remainingDays = max(1, daysInMonth - currentDay + 1)
        let remainingBudget = max(0, monthlyBudget - totalSpent)
        return remainingBudget / remainingDays
    }

    public static func calculateMilkTotal(logs: [MilkBottleLog], rate: Int) -> (liters: Double, bill: Int) {
        let totalLiters = logs.reduce(0.0) { $0 + $1.status.rawValue }
        let bill = Int(totalLiters * Double(rate))
        return (totalLiters, bill)
    }

    public static func calculateStaffPayroll(
        baseSalary: Int,
        daysInMonth: Int,
        presentDays: Int,
        advanceTaken: Int
    ) -> Int {
        guard daysInMonth > 0 else { return 0 }
        let perDayRate = Double(baseSalary) / Double(daysInMonth)
        let earned = Int(perDayRate * Double(presentDays))
        return max(0, earned - advanceTaken)
    }

    public static func formatInr(_ amount: Int) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹ "
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: amount)) ?? "₹ \(amount)"
    }
}
