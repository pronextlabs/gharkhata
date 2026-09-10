import Foundation

public enum CategoryType: Int, CaseIterable, Identifiable {
    case vegetables = 1
    case groceries = 2
    case milk = 3
    case maid = 4
    case gasUtility = 5
    case kids = 6
    case medical = 7
    case poojaShagun = 8

    public var id: Int { rawValue }

    public var titleEn: String {
        switch self {
        case .vegetables: return "Sabzi Mandi"
        case .groceries: return "Kirana / Ration"
        case .milk: return "Doodh & Dairy"
        case .maid: return "Kamwali / Bai"
        case .gasUtility: return "Gas & Bijli"
        case .kids: return "Baccho Ki Padhai"
        case .medical: return "Dawai & Doctor"
        case .poojaShagun: return "Pooja & Shagun"
        }
    }

    public var emoji: String {
        switch self {
        case .vegetables: return "🥦"
        case .groceries: return "🌾"
        case .milk: return "🥛"
        case .maid: return "🧹"
        case .gasUtility: return "⛽"
        case .kids: return "📚"
        case .medical: return "💊"
        case .poojaShagun: return "🪔"
        }
    }
}

public enum MilkStatus: Double, CaseIterable {
    case fullLiter = 1.0
    case literAndHalf = 1.5
    case twoLiters = 2.0
    case noMilk = 0.0

    public var label: String {
        switch self {
        case .fullLiter: return "1.0L"
        case .literAndHalf: return "1.5L"
        case .twoLiters: return "2.0L"
        case .noMilk: return "0L (Bandh)"
        }
    }

    public func next() -> MilkStatus {
        switch self {
        case .fullLiter: return .literAndHalf
        case .literAndHalf: return .twoLiters
        case .twoLiters: return .noMilk
        case .noMilk: return .fullLiter
        }
    }
}

public struct MilkBottleLog: Identifiable {
    public let id = UUID()
    public let dayOfMonth: Int
    public var status: MilkStatus = .fullLiter
    public let ratePerLiter: Int = 66
}

public struct CashGalla {
    public var note500: Int = 0
    public var note200: Int = 0
    public var note100: Int = 0
    public var note50: Int = 0

    public var totalInr: Int {
        (note500 * 500) + (note200 * 200) + (note100 * 100) + (note50 * 50)
    }
}
