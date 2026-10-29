export function barWidthPercent(value: number, max: number): number {
    if (max <= 0 || value <= 0) return 0;
    return Math.max((value / max) * 100, 2);
}