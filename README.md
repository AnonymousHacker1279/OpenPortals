![OpenPortals Logo](logo.png)

## A simple-to-use library for implementing custom dimension portals.

OpenPortals is a lightweight library mod that makes it easy to create custom portals. It's ideal for 
[Jar-in-Jar](https://docs.neoforged.net/toolchain/docs/dependencies/jarinjar) purposes.

### Setup
To use OpenPortals in your mod, add it as a dependency in your `build.gradle` file:

```gradle
repositories {
	maven {
		name "AnonymousHacker1279"
		url "https://maven.anonymoushacker1279.tech/releases"
	}

}

dependencies {
    implementation "tech.anonymoushacker1279.openportals:OpenPortals:<version>"
}
```

The latest version can be found in the releases section of the repository.

### Usage
Creating a portal can be done in a single step during mod setup, for example:

```java
@SubscribeEvent
public static void onCommonStartUp(FMLCommonSetupEvent event) {
    new CustomPortalBuilder()
            .frame(Blocks.GLOWSTONE)
            .destination(Identifier.withDefaultNamespace("the_nether"))
            .lightWithFluid(Fluids.WATER)
            .tintColor(255, 0, 255)
            .build();
}
```

All portal builder methods are documented, so refer to the Javadocs for additional information.

## License

OpenPortals is [MIT licensed](LICENSE).