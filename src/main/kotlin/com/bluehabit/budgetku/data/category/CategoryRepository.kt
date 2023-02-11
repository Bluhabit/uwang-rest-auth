package com.bluehabit.budgetku.data.category

import org.springframework.data.repository.PagingAndSortingRepository

interface CategoryRepository:PagingAndSortingRepository<Category,String> {
}