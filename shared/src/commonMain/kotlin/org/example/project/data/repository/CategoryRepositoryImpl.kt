package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.CategoryDao
import org.example.project.domain.model.Category
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllCategoriesOnce(): List<Category> =
        categoryDao.getAllCategoriesOnce().map { it.toDomain() }

    override fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    override suspend fun insertCategories(categories: List<Category>) =
        categoryDao.insertCategories(categories.map { it.toEntity() })

    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category.toEntity())

    override suspend fun deleteCategoryById(id: Long) =
        categoryDao.deleteCategoryById(id)

    override suspend fun getCategoryCount(): Int =
        categoryDao.getCategoryCount()
}
