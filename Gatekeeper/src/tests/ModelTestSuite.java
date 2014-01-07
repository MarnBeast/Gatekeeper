package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	ClipTest.class,
	ClipTimeTest.class,
	LandmarkTest.class,
	SettingsTest.class,
	TapeTest.class
})
public class ModelTestSuite {

}
