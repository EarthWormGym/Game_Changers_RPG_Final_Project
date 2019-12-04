require "spec_helper"
feature "battle" do
  scenario "I should be able to visit the battle page" do
    visit '/battle'
    expect(page).to have_content 'Enemy'
  end
  scenario "should be able to use health potions" do
    visit '/class'
    find('#archerclas').click
    visit '/battle'
    click_button("Use a Healing potion")
    expect(page).to have_content 'recovered 25 health points'
  end
  scenario "should be able to use poison potions" do
  visit '/class'
      find('#archerclas').click
      visit '/battle'
      click_button("Use a Poison Potion")
      expect(page).to have_content 'for 20 damage'
    end
end
