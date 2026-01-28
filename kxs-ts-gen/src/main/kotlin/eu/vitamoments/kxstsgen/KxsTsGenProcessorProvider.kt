package eu.vitamoments.kxstsgen

import com.google.devtools.ksp.processing.*

class KxsTsGenProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KxsTsGenProcessor(
            logger = environment.logger,
            options = environment.options
        )
    }
}
