const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read tasks", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("tasks").get());
});

test("Authenticated user: cannot read another user's task", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("tasks").doc("task_bob").set({
      id: "task_bob",
      userId: BOB_UID,
      title: "Bob's PCR Analysis",
      status: "TODO",
      priority: "HIGH",
      category: "BIOLOGY",
      scienceSkill: "EXPERIMENT_DESIGN",
      isCompleted: false,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("tasks").doc("task_bob").get());
});

test("Alice can create and read her own valid scientific task", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const taskRef = aliceDb.collection("users").doc(ALICE_UID).collection("tasks").doc("task_alice");
  
  await assertSucceeds(taskRef.set({
    id: "task_alice",
    userId: ALICE_UID,
    title: "Enzyme Kinetics Titration",
    description: "Measure rate of reaction vs substrate concentration",
    status: "IN_PROGRESS",
    priority: "HIGH",
    category: "CHEMISTRY",
    scienceSkill: "DATA_COLLECTION",
    isCompleted: false,
    hypothesis: "Vmax will double if enzyme concentration doubles",
    independentVariable: "Substrate Concentration (mM)",
    dependentVariable: "Initial Velocity (umol/min)",
  }));

  await assertSucceeds(taskRef.get());
});

test("Alice cannot create invalid task with unknown status", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const taskRef = aliceDb.collection("users").doc(ALICE_UID).collection("tasks").doc("task_bad");
  
  await assertFails(taskRef.set({
    id: "task_bad",
    userId: ALICE_UID,
    title: "Bad Task",
    status: "INVALID_STATUS",
    priority: "HIGH",
    category: "CHEMISTRY",
    scienceSkill: "DATA_COLLECTION",
    isCompleted: false,
  }));
});
