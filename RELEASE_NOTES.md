This is a major internal release with significant changes to the codebase and architecture.
There are no user-facing changes under the API package.

### Improvements
- Improved portal linking storage efficiency; now uses bidirectional links instead of storing duplicate entries
- Optimized portal frame validation logic for better performance
- Improved portal block caching to reduce redundant lookups
- Added automatic cleanup of orphaned/invalid portal links on server shutdown
- Better null safety throughout the codebase

### Bugfixes
- Fixed portal blocks missing tint indices
- Fixed potential memory leaks from orphaned dimension links