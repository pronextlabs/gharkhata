import SwiftUI

public struct ContentView: View {
    @State private var selectedTab = 0

    public init() {}

    public var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Label("Aaj Ka Hisaab", systemImage: "house.fill")
                }
                .tag(0)

            ServicesView()
                .tabItem {
                    Label("Ghar Ke Kaam", systemImage: "drop.fill")
                }
                .tag(1)

            SavingsView()
                .tabItem {
                    Label("Bachat", systemImage: "lock.shield.fill")
                }
                .tag(2)

            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gearshape.fill")
                }
                .tag(3)
        }
        .accentColor(Color(red: 0.77, green: 0.36, blue: 0.24)) // Terracotta
    }
}

struct SettingsView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Spacer()
                Text("GharKhata")
                    .font(.title).bold()
                    .foregroundColor(Color(red: 0.77, green: 0.36, blue: 0.24))
                Text("Version 1.1.0")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                Text("Crafted with ❤️ by pronextlabs")
                    .font(.callout).bold()
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color(red: 0.99, green: 0.95, blue: 0.94))
                    .cornerRadius(20)

                Text("100% Offline • Zero Analytics • Free Public Good")
                    .font(.footnote)
                    .foregroundColor(.secondary)

                Link("Visit pronextlabs on GitHub", destination: URL(string: "https://github.com/pronextlabs/gharkhata")!)
                    .font(.subheadline).bold()
                    .padding()
                Spacer()
            }
            .navigationTitle("Settings")
        }
    }
}

struct HomeView: View {
    @State private var inputExpression = ""
    @State private var selectedCategory: CategoryType = .vegetables
    @State private var monthlyBudget = 0
    @State private var totalSpent = 0

    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.99, green: 0.98, blue: 0.97).ignoresSafeArea()

                VStack(spacing: 16) {
                    // Pacer Card
                    VStack(spacing: 6) {
                        Text("Aaj Ka Safe Kharcha")
                            .font(.subheadline)
                            .foregroundColor(.white.opacity(0.85))
                        Text(AutoCalculators.formatInr(monthlyBudget == 0 ? 0 : max(0, (monthlyBudget - totalSpent) / 30)))
                            .font(.system(size: 38, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                        Text(monthlyBudget == 0 ? "Tap to set monthly budget" : "Dynamic daily safe spend")
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.75))
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 20)
                    .background(Color(red: 0.77, green: 0.36, blue: 0.24))
                    .cornerRadius(16)

                    // Category Bar
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ForEach(CategoryType.allCases) { cat in
                                Button(action: { selectedCategory = cat }) {
                                    VStack(spacing: 4) {
                                        Text(cat.emoji).font(.title2)
                                        Text(cat.titleEn).font(.caption2).bold()
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(selectedCategory == cat ? Color(red: 0.99, green: 0.95, blue: 0.94) : Color.white)
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(selectedCategory == cat ? Color(red: 0.77, green: 0.36, blue: 0.24) : Color.gray.opacity(0.2), lineWidth: 1.5)
                                    )
                                }
                            }
                        }
                    }

                    Spacer()

                    // Keypad Display
                    HStack {
                        Text("\(selectedCategory.emoji) \(selectedCategory.titleEn)")
                            .foregroundColor(.secondary)
                        Spacer()
                        Text(inputExpression.isEmpty ? "₹ 0" : "₹ \(inputExpression)")
                            .font(.system(size: 28, weight: .bold))
                    }
                    .padding()
                    .background(Color.white)
                    .cornerRadius(12)

                    // Mandi Calc Keypad
                    VStack(spacing: 8) {
                        ForEach([["1", "2", "3", "+"], ["4", "5", "6", "-"], ["7", "8", "9", "⌫"], ["C", "0", "00", "✓"]], id: \.self) { row in
                            HStack(spacing: 8) {
                                ForEach(row, id: \.self) { key in
                                    Button(action: {
                                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                        if key == "C" { inputExpression = "" }
                                        else if key == "⌫" { if !inputExpression.isEmpty { inputExpression.removeLast() } }
                                        else if key == "✓" { inputExpression = "" }
                                        else { inputExpression += key }
                                    }) {
                                        Text(key)
                                            .font(.title3).bold()
                                            .frame(maxWidth: .infinity, minHeight: 50)
                                            .background(key == "✓" ? Color(red: 0.18, green: 0.44, blue: 0.25) : Color.white)
                                            .foregroundColor(key == "✓" ? .white : .primary)
                                            .cornerRadius(10)
                                    }
                                }
                            }
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("GharKhata")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct ServicesView: View {
    var body: some View {
        NavigationView {
            Text("🥛 Doodh & 🧹 Kamwali Attendance")
                .navigationTitle("Ghar Ke Kaam")
        }
    }
}

struct SavingsView: View {
    var body: some View {
        NavigationView {
            Text("🔒 Gupt Tijori & 💵 Cash Galla")
                .navigationTitle("Bachat")
        }
    }
}
