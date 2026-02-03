# API Rate Limits (Free Accounts)

Gold Buddy uses external APIs to fetch live gold rates and exchange rates. Below are the limitations for the free tiers.

## 1. GoldAPI (goldapi.io)
Used for fetching global gold spot prices (XAU).

*   **Monthly Limit**: 100 requests.
*   **Daily Average**: ~3 requests per day.
*   **Update Interval**: 2-second interval data availability.
*   **Impact**: Manual refreshes and frequent app restarts will quickly exhaust this quota. Caching is highly recommended.

## 2. ExchangeRate-API
Used for converting USD gold prices to PKR.

*   **Monthly Limit**: 1,500 requests.
*   **Daily Average**: ~50 requests per day.
*   **Update Frequency**: Refreshes once every 24 hours in the free tier.

---

> [!WARNING]
> Exceeding these limits will cause the app to show an "Error" state until the monthly quota resets.
