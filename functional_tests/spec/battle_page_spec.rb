require "spec_helper"
feature "battle" do
  scenario "I should be able to visit the battle page" do
    visit '/battle'
    expect(page).to have_content 'Welcome to the Makers and Mortal'
  end
end
