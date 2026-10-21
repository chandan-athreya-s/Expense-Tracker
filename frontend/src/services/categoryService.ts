import { apiGet } from "./apiClient";
import type { Category } from "../types/category";

export function getCategories(): Promise<Category[]> {
    return apiGet<Category[]>("/api/categories");
}